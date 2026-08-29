package lu.kbra.school_lu.endpoints;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import lu.kbra.school_lu.db.data.ExamAttachmentData;
import lu.kbra.school_lu.db.data.ExamData;
import lu.kbra.school_lu.db.data.ExerciseAttachmentData;
import lu.kbra.school_lu.db.data.ExerciseData;
import lu.kbra.school_lu.db.data.SectionData;
import lu.kbra.school_lu.db.data.SubjectData;
import lu.kbra.school_lu.db.table.ExamAttachmentTable;
import lu.kbra.school_lu.db.table.ExamTable;
import lu.kbra.school_lu.db.table.ExerciseAttachmentTable;
import lu.kbra.school_lu.db.table.ExerciseTable;
import lu.kbra.school_lu.db.table.SectionTable;
import lu.kbra.school_lu.db.table.SubjectTable;
import lu.kbra.school_lu.service.UserPermissionService;

@RestController
public class SyncExercisesController {

	private final SectionTable sectionTable;
	private final SubjectTable subjectTable;
	private final ExamTable examTable;
	private final ExamAttachmentTable examAttachmentTable;
	private final ExerciseTable exerciseTable;
	private final ExerciseAttachmentTable exerciseAttachmentTable;
	private final UserPermissionService userPermissionService;
	private final Executor executor;

	public SyncExercisesController(
			final SectionTable sectionTable,
			final SubjectTable subjectTable,
			final ExamTable examTable,
			final ExamAttachmentTable examAttachmentTable,
			final ExerciseTable exerciseTable,
			final ExerciseAttachmentTable exerciseAttachmentTable,
			final UserPermissionService userPermissionService,
			@Qualifier("applicationTaskExecutor") final Executor executor) {
		this.sectionTable = sectionTable;
		this.subjectTable = subjectTable;
		this.examTable = examTable;
		this.examAttachmentTable = examAttachmentTable;
		this.exerciseTable = exerciseTable;
		this.exerciseAttachmentTable = exerciseAttachmentTable;
		this.userPermissionService = userPermissionService;
		this.executor = executor;
	}

	@PostMapping("/exam-db/exercises/update-index")
	public SseEmitter updateIndex(
			@AuthenticationPrincipal final UserId userId,
			@RequestParam final MultipartFile file,
			@RequestParam final boolean allowSectionCreation,
			@RequestParam final boolean allowSubjectCreation,
			@RequestParam final boolean allowExamCreation,
			@RequestParam final boolean allowExamAttachmentCreation) {

		this.userPermissionService.requireAllPermissions(userId, UserPermissionType.MANAGE_EXERCISE);

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

				final Set<String> requiredHeaders = Set.of("Section",
						"Subject",
						"Year",
						"Retry",
						"Season",
						"Source",
						"Exercise Index",
						"Qualifier",
						"Alternative Index",
						"Additive box",
						"Subtractive boxes",
						"Attachment");

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

				final Pattern rectanglePattern = Pattern.compile(
						"\\(\\(\\s*([+-]?\\d*\\.?\\d+)\\s*,\\s*([+-]?\\d*\\.?\\d+)\\s*\\),\\s*\\(\\s*([+-]?\\d*\\.?\\d+)\\s*,\\s*([+-]?\\d*\\.?\\d+)\\s*\\)\\)");

				final Function<String, Rectangle2D.Float> parseRectangle = value -> {
					final Matcher matcher = rectanglePattern.matcher(value.trim());

					if (!matcher.matches()) {
						throw new IllegalArgumentException("Invalid rectangle: " + value);
					}

					final float x1 = Float.parseFloat(matcher.group(1));
					final float y1 = Float.parseFloat(matcher.group(2));
					final float x2 = Float.parseFloat(matcher.group(3));
					final float y2 = Float.parseFloat(matcher.group(4));

					return new Rectangle2D.Float(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
				};

				int index = 0;
				for (final CSVRecord record : records) {
					index++;

					final String section = record.get("Section").toUpperCase();
					final String subject = record.get("Subject").toUpperCase();
					final int year = Integer.parseInt(record.get("Year"));
					final int season = PCUtils.parseInteger(record.get("Season"),
							() -> "SEPT".equalsIgnoreCase(record.get("Season")) ? 9 : 6);
					final boolean retry = PCUtils.parseBoolean(record.get("Retry"), () -> "YES".equalsIgnoreCase(record.get("Retry")));

					final String source = PCUtils.nullIfBlank(record.get("Source"));
					final int exerciseIndex = Integer.parseInt(record.get("Exercise Index"));
					final String qualifier = PCUtils.nullIfBlank(record.get("Qualifier"));
					final int alternativeIndex = Integer.parseInt(record.get("Alternative Index"));
					final String additiveBox = PCUtils.nullIfBlank(record.get("Additive box"));
					final String subtractiveBoxes = PCUtils.nullIfBlank(record.get("Subtractive boxes"));
					final String attachment = record.get("Attachment");

					if (source == null) {
						emitter.send(
								SseEmitter.event().name("warning").data("Exercise with no source: " + Arrays.toString(record.values())));
						continue;
					}

					if (additiveBox == null) {
						emitter.send(SseEmitter.event()
								.name("warning")
								.data("Exercise with no additive box: " + Arrays.toString(record.values())));
						continue;
					}

					final SectionData sectionData;
					final SubjectData subjectData;
					final ExamData examData;
					final ExamAttachmentData examAttachment;

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

					try {
						examData = allowExamCreation
								? this.examTable.loadUniqueIfExistsElseInsert(new ExamData(subjectData.getId(), year, season, retry))
								: this.examTable.loadUnique(new ExamData(subjectData.getId(), year, season, retry));
					} catch (final NoMatchingRowException e) {
						emitter.send(SseEmitter.event().name("warning").data("Subject not found: " + subject));
						continue;
					}

					try {
						examAttachment = allowExamCreation
								? this.examAttachmentTable
										.loadUniqueIfExistsElseInsert(new ExamAttachmentData(examData.getId(), qualifier, null, source))
								: this.examAttachmentTable.loadUnique(new ExamAttachmentData(examData.getId(), qualifier, null, source));
					} catch (final NoMatchingRowException e) {
						emitter.send(SseEmitter.event().name("warning").data("Exam attachment not found: " + source));
						continue;
					}

					final ExerciseData exerciseData = this.exerciseTable
							.loadUniqueIfExistsElseInsert(new ExerciseData(examData.getId(), exerciseIndex));

					final Rectangle2D.Float additiveRectangle = parseRectangle.apply(additiveBox);

					final Rectangle2D.Float[] subtractiveRectangles;

					if (subtractiveBoxes == null) {
						subtractiveRectangles = null;
					} else {
						final String[] boxes = subtractiveBoxes.split(";");
						subtractiveRectangles = new Rectangle2D.Float[boxes.length];
						for (int i = 0; i < boxes.length; i++) {
							subtractiveRectangles[i] = parseRectangle.apply(boxes[i]);
						}
					}

					final ExerciseAttachmentData exerciseAttachment = new ExerciseAttachmentData(exerciseData.getId(),
							qualifier,
							alternativeIndex,
							attachment,
							examAttachment.getId(),
							additiveRectangle,
							subtractiveRectangles);

					if (!this.exerciseAttachmentTable.existsUnique(exerciseAttachment)) {
						this.exerciseAttachmentTable.insertAndReload(exerciseAttachment);
					}

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
