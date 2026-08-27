package lu.kbra.school_lu.endpoints;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.exception.NoMatchingRowException;
import lu.kbra.school_lu.data.UserId;
import lu.kbra.school_lu.data.UserPermissionType;
import lu.kbra.school_lu.db.data.ExamAttachementData;
import lu.kbra.school_lu.db.data.ExamData;
import lu.kbra.school_lu.db.data.SectionData;
import lu.kbra.school_lu.db.data.SubjectData;
import lu.kbra.school_lu.db.table.ExamAttachementTable;
import lu.kbra.school_lu.db.table.ExamTable;
import lu.kbra.school_lu.db.table.SectionTable;
import lu.kbra.school_lu.db.table.SubjectTable;
import lu.kbra.school_lu.service.UserPermissionService;

@RestController
public class SyncExamsController {

	private final SectionTable sectionTable;
	private final SubjectTable subjectTable;
	private final ExamTable examTable;
	private final ExamAttachementTable examAttachementTable;
	private final UserPermissionService userPermissionService;
	private final Executor executor;

	public SyncExamsController(
			final SectionTable sectionTable,
			final SubjectTable subjectTable,
			final ExamTable examTable,
			final ExamAttachementTable examAttachementTable,
			final UserPermissionService userPermissionService,
			@Qualifier("applicationTaskExecutor") final Executor executor) {
		this.sectionTable = sectionTable;
		this.subjectTable = subjectTable;
		this.examTable = examTable;
		this.examAttachementTable = examAttachementTable;
		this.userPermissionService = userPermissionService;
		this.executor = executor;
	}

	@PostMapping("/exam-db/update-index")
	public SseEmitter updateIndex(
			@AuthenticationPrincipal final UserId userId,
			@RequestParam final MultipartFile file,
			@RequestParam final boolean allowSectionCreation,
			@RequestParam final boolean allowSubjectCreation) {

		this.userPermissionService.requireAllPermissions(userId,
				UserPermissionType.MANAGE_EXAM,
				UserPermissionType.MANAGE_SECTION,
				UserPermissionType.MANAGE_SUBJECT);

		final SseEmitter emitter = new SseEmitter(0L);

		if (file.isEmpty()) {
			try {
				emitter.send(SseEmitter.event().name("error").data("File is empty"));
			} catch (final IOException ignored) {
			}
			emitter.complete();
			return emitter;
		}

		final String filename = file.getOriginalFilename();
		if (filename == null || !filename.endsWith(".csv")) {
			try {
				emitter.send(SseEmitter.event().name("error").data("Only CSV files are allowed"));
			} catch (final IOException ignored) {
			}
			emitter.complete();
			return emitter;
		}

		this.executor.execute(() -> {
			try {
				final CSVParser parser = CSVParser.parse(file.getInputStream(),
						StandardCharsets.UTF_8,
						CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).get());

				final Set<String> requiredHeaders = Set
						.of("Section", "Subject", "Year", "Season", "Retry", "Name", "Mission statement", "Solution", "Data", "Oral");

				final Set<String> headers = new HashSet<>(parser.getHeaderNames());

				if (!headers.equals(requiredHeaders)) {
					final Set<String> missingHeaders = new HashSet<>(requiredHeaders);
					missingHeaders.removeAll(headers);

					final Set<String> unexpectedHeaders = new HashSet<>(headers);
					unexpectedHeaders.removeAll(requiredHeaders);

					emitter.send(SseEmitter.event()
							.name("error")
							.data("Invalid headers. Missing: " + missingHeaders + ", unexpected: " + unexpectedHeaders));

					emitter.complete();
					return;
				}

				final List<CSVRecord> records = parser.getRecords();

				final int rowCount = records.size();

				final Map<String, SectionData> sectionDatas = new HashMap<>();
				final Map<String, Map<String, SubjectData>> subjectDatas = new HashMap<>();

				int index = 0;
				for (final CSVRecord record : records) {
					index++;

					final String section = record.get("Section").toUpperCase();
					final String subject = record.get("Subject").toUpperCase();
					final int year = Integer.parseInt(record.get("Year"));
					final int season = PCUtils.parseInteger(record.get("Season"),
							() -> "SEPT".equalsIgnoreCase(record.get("Season")) ? 9 : 6);
					final boolean retry = PCUtils.parseBoolean(record.get("Retry"),
							() -> "YES".equalsIgnoreCase(record.get("Retry")) == true);
					final String name = PCUtils.nullIfBlank(record.get("Name"));
					final String statement = PCUtils.nullIfBlank(record.get("Mission statement"));
					final String solution = PCUtils.nullIfBlank(record.get("Solution"));
					final String data = PCUtils.nullIfBlank(record.get("Data"));
					final String oral = PCUtils.nullIfBlank(record.get("Oral"));

					if (statement == null && solution == null && data == null && oral == null) {
						emitter.send(
								SseEmitter.event().name("warning").data("Exam with no attachements: " + Arrays.toString(record.values())));
						continue;
					}

					final SectionData sectionData;
					final SubjectData subjectData;
					final ExamData examData;

					try {
						sectionData = sectionDatas.computeIfAbsent(section,
								k -> allowSectionCreation ? this.sectionTable.loadUniqueIfExistsElseInsert(new SectionData(k))
										: this.sectionTable.loadUnique(new SectionData(k)));
					} catch (final NoMatchingRowException e) {
						emitter.send(SseEmitter.event().name("warning").data("Section not found: " + section));
						continue;
					}

					try {
						subjectData = subjectDatas.computeIfAbsent(section, k -> new HashMap<>())
								.computeIfAbsent(subject,
										k -> allowSubjectCreation
												? this.subjectTable.loadUniqueIfExistsElseInsert(new SubjectData(sectionData.getId(), k))
												: this.subjectTable.loadUnique(new SubjectData(sectionData.getId(), k)));
					} catch (final NoMatchingRowException e) {
						emitter.send(SseEmitter.event().name("warning").data("Subject not found: " + subject));
						continue;
					}

					examData = this.examTable.loadUniqueIfExistsElseInsert(new ExamData(subjectData.getId(), year, season, retry));

					final BiConsumer<String, String> storeAttachement = (qualifier, path) -> {
						if (path == null) {
							return;
						}

						final ExamAttachementData statementData = new ExamAttachementData(examData.getId(), qualifier, name, path);
						if (!this.examAttachementTable.existsUnique(statementData)) {
							this.examAttachementTable.insertAndReload(statementData);
						}
					};

					storeAttachement.accept("STATEMENT", statement);
					storeAttachement.accept("SOLUTION", solution);
					storeAttachement.accept("DATA", data);
					storeAttachement.accept("ORAL", oral);

					emitter.send(SseEmitter.event().name("progress").data(index + "/" + rowCount));
				}

				emitter.send(SseEmitter.event().name("complete").data("CSV uploaded successfully"));

				emitter.complete();
			} catch (final IOException e) {
				try {
					emitter.send(SseEmitter.event().name("error").data("Error reading CSV"));
				} catch (final IOException ignored) {
				}

				emitter.completeWithError(e);
			}
		});

		return emitter;
	}

}
