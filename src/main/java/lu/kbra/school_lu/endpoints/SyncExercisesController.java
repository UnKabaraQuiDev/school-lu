package lu.kbra.school_lu.endpoints;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.exception.NoMatchingRowException;
import lu.kbra.pclib.db.impl.DeferredDBTransaction;
import lu.kbra.pclib.db.transaction.DefaultTransactionOption;
import lu.kbra.school_lu.data.CurrentUser;
import lu.kbra.school_lu.data.ExamAttachmentType;
import lu.kbra.school_lu.data.ExamSeason;
import lu.kbra.school_lu.data.ExamType;
import lu.kbra.school_lu.data.UserPermissionType;
import lu.kbra.school_lu.db.data.ExamAttachmentData;
import lu.kbra.school_lu.db.data.ExamData;
import lu.kbra.school_lu.db.data.ExamPartData;
import lu.kbra.school_lu.db.data.ExamPartExamData;
import lu.kbra.school_lu.db.data.ExerciseAttachmentData;
import lu.kbra.school_lu.db.data.ExerciseData;
import lu.kbra.school_lu.db.data.ExerciseTagData;
import lu.kbra.school_lu.db.data.SectionData;
import lu.kbra.school_lu.db.data.SubjectData;
import lu.kbra.school_lu.db.data.TagData;
import lu.kbra.school_lu.db.data.UserData;
import lu.kbra.school_lu.db.table.ExamAttachmentTable;
import lu.kbra.school_lu.db.table.ExamPartExamTable;
import lu.kbra.school_lu.db.table.ExamPartTable;
import lu.kbra.school_lu.db.table.ExamTable;
import lu.kbra.school_lu.db.table.ExerciseAttachmentTable;
import lu.kbra.school_lu.db.table.ExerciseTable;
import lu.kbra.school_lu.db.table.ExerciseTagTable;
import lu.kbra.school_lu.db.table.SectionTable;
import lu.kbra.school_lu.db.table.SubjectTable;
import lu.kbra.school_lu.db.table.TagTable;
import lu.kbra.school_lu.service.UserPermissionService;

@RestController
public class SyncExercisesController {

	private final SectionTable sectionTable;
	private final SubjectTable subjectTable;
	private final ExamTable examTable;
	private final ExamPartExamTable examPartExamTable;
	private final ExamPartTable examPartTable;
	private final ExamAttachmentTable examAttachmentTable;
	private final ExerciseTable exerciseTable;
	private final ExerciseAttachmentTable exerciseAttachmentTable;
	private final ExerciseTagTable exerciseTagTable;
	private final TagTable tagTable;
	private final UserPermissionService userPermissionService;
	private final Executor executor;

	public SyncExercisesController(
			final SectionTable sectionTable,
			final SubjectTable subjectTable,
			final ExamTable examTable,
			final ExamPartExamTable examPartExamTable,
			final ExamPartTable examPartTable,
			final ExamAttachmentTable examAttachmentTable,
			final ExerciseTable exerciseTable,
			final ExerciseAttachmentTable exerciseAttachmentTable,
			final ExerciseTagTable exerciseTagTable,
			final TagTable tagTable,
			final UserPermissionService userPermissionService,
			@Qualifier("applicationTaskExecutor") final Executor executor) {
		this.sectionTable = sectionTable;
		this.subjectTable = subjectTable;
		this.examTable = examTable;
		this.examPartExamTable = examPartExamTable;
		this.examPartTable = examPartTable;
		this.examAttachmentTable = examAttachmentTable;
		this.exerciseTable = exerciseTable;
		this.exerciseAttachmentTable = exerciseAttachmentTable;
		this.exerciseTagTable = exerciseTagTable;
		this.tagTable = tagTable;
		this.userPermissionService = userPermissionService;
		this.executor = executor;
	}

	@PostMapping("/exam-db/exercises/update-index")
	public SseEmitter updateIndex(
			@CurrentUser final UserData userData,
			@RequestParam final MultipartFile file,
			@RequestParam final boolean allowSectionCreation,
			@RequestParam final boolean allowSubjectCreation,
			@RequestParam final boolean allowExamCreation,
			@RequestParam final boolean allowExamAttachmentCreation,
			@RequestParam final boolean allowTagCreation) {

		this.userPermissionService.requireAllPermissions(userData, UserPermissionType.MANAGE_EXERCISE);

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
			try (DeferredDBTransaction transaction = this.examTable.getDatabase()
					.createTransaction(c -> c.enable(DefaultTransactionOption.DEFER_FOREIGN_KEYS))) {
				final CSVParser parser = CSVParser.parse(file.getInputStream(),
						StandardCharsets.UTF_8,
						CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).get());

				final Set<String> requiredHeaders = Set.of("Section",
						"Subject",
						"Year",
						"Subtype",
						"Season",
						"Name",
						"Source",
						"Exercise Index",
						"Qualifier",
						"Alternative Index",
						"Additive box",
						"Subtractive boxes",
						"Attachment",
						"SourceExam",
						"Tags");

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

				final SectionTable sectionTable = transaction.use(this.sectionTable);
				final SubjectTable subjectTable = transaction.use(this.subjectTable);
				final ExamTable examTable = transaction.use(this.examTable);
				final ExamPartExamTable examPartExamTable = transaction.use(this.examPartExamTable);
				final ExamPartTable examPartTable = transaction.use(this.examPartTable);
				final ExamAttachmentTable examAttachmentTable = transaction.use(this.examAttachmentTable);

				int index = 0;
				for (final CSVRecord record : records) {
					index++;

					final String section = record.get("Section").toUpperCase();
					final String subject = record.get("Subject").toUpperCase();
					final int year = Integer.parseInt(record.get("Year"));
					final ExamSeason season = switch (record.get("Season")) {
					case "ETE", "SUMMER" -> ExamSeason.SUMMER;
					case "SEPT" -> ExamSeason.SEPTEMBER;
					default -> null;
					};
					final ExamType subtype = switch (record.get("Subtype")) {
					case "NORMAL" -> ExamType.NORMAL;
					case "REP" -> ExamType.REP;
					case "AJOU" -> ExamType.AJOU;
					default -> null;
					};
					final String name = PCUtils.nullIfBlank(record.get("Name"));
					final String attachement = PCUtils.nullIfBlank(record.get("Source")); // Attachement
					final int exerciseIndex = Integer.parseInt(record.get("Exercise Index"));
					final ExamAttachmentType qualifier = switch (record.get("Qualifier").trim().toUpperCase()) {
					case "SOLUTION" -> ExamAttachmentType.SOLUTION;
					case "STATEMENT" -> ExamAttachmentType.STATEMENT;
					case "ORAL" -> ExamAttachmentType.ORAL;
					case "DATA" -> ExamAttachmentType.DATA;
					default -> null;
					};
					final int alternativeIndex = Integer.parseInt(record.get("Alternative Index"));
					final String additiveBox = PCUtils.nullIfBlank(record.get("Additive box"));
					final String subtractiveBoxes = PCUtils.nullIfBlank(record.get("Subtractive boxes"));
					final String attachment = record.get("Attachment");
					final String source = PCUtils.nullIfBlank(record.get("SourceExam"));
					final String[] tags = record.get("Tags").split("\\s+");

					if (attachement == null) {
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
					if (qualifier == null) {
						emitter.send(SseEmitter.event().name("warning").data("Unknown qualifier: " + Arrays.toString(record.values())));
						continue;
					}

					final SectionData sectionData;
					final SubjectData subjectData;
					final ExamData examData;
					ExamPartData examPartData;
					final ExamAttachmentData examAttachmentData;

					try {
						sectionData = sectionDatas.computeIfAbsent(section,
								k -> allowSectionCreation ? sectionTable.loadUniqueIfExistsElseInsert(new SectionData(k))
										: sectionTable.loadUnique(new SectionData(k)));
					} catch (final NoMatchingRowException e) {
						emitter.send(SseEmitter.event().name("warning").data("Section not found: " + section));
						continue;
					}

					try {
						subjectData = subjectDatas.computeIfAbsent(section, k -> new HashMap<>())
								.computeIfAbsent(subject,
										k -> allowSubjectCreation
												? subjectTable.loadUniqueIfExistsElseInsert(new SubjectData(sectionData.getId(), k))
												: subjectTable.loadUnique(new SubjectData(sectionData.getId(), k)));
					} catch (final NoMatchingRowException e) {
						emitter.send(SseEmitter.event().name("warning").data("Subject not found: " + subject));
						continue;
					}

					try {
						examData = allowExamCreation
								? examTable.loadUniqueIfExistsElseInsert(new ExamData(subjectData.getId(), year, season, subtype))
								: examTable.loadUnique(new ExamData(subjectData.getId(), year, season, subtype));
					} catch (final NoMatchingRowException e) {
						emitter.send(SseEmitter.event().name("warning").data("Exam not found: " + subject));
						continue;
					}

					if (source != null) {
						final String[] sourceTokens = source.split(":");
						final String sourceSection = sourceTokens[0];
						final String sourceSubject = sourceTokens[1];
						final int sourceYear = Integer.parseInt(sourceTokens[2]);
						final ExamSeason sourceSeason = switch (sourceTokens[3]) {
						case "ETE", "SUMMER" -> ExamSeason.SUMMER;
						case "SEPT" -> ExamSeason.SEPTEMBER;
						default -> null;
						};
						final ExamType sourceSubtype = switch (sourceTokens[4]) {
						case "NORMAL" -> ExamType.NORMAL;
						case "REP" -> ExamType.REP;
						case "AJOU" -> ExamType.AJOU;
						default -> null;
						};
						final String sourceName = PCUtils.nullIfBlank(sourceTokens[5]);
						final ExamAttachmentType sourceQualifier = switch (sourceTokens[6]) {
						case "SOLUTION" -> ExamAttachmentType.SOLUTION;
						case "STATEMENT" -> ExamAttachmentType.STATEMENT;
						case "ORAL" -> ExamAttachmentType.ORAL;
						case "DATA" -> ExamAttachmentType.DATA;
						default -> null;
						};

						final ExamData parentExamData = examTable
								.bySectionSubjectYearSeasonSubtype(sourceSection, sourceSubject, sourceYear, sourceSeason, sourceSubtype);
						if (parentExamData == null) {
							emitter.send(SseEmitter.event().name("warning").data("Parent exam not found: " + source));
							continue;
						}

						examAttachmentData = examAttachmentTable.byExamAndPartNameAndQualifier(parentExamData, sourceName, sourceQualifier);
						if (examAttachmentData == null) {
							emitter.send(SseEmitter.event().name("warning").data("Parent exam attachement not found: " + source));
							continue;
						}

						examPartData = examPartTable.byAttachmentAndExam(examAttachmentData, parentExamData);
						if (examPartData == null) {
							emitter.send(SseEmitter.event().name("warning").data("Parent exam part not found: " + source));
							continue;
						}

						examPartExamTable.loadIfExistsElseInsert(new ExamPartExamData(examPartData.getId(), parentExamData.getId()));
					} else {
						examAttachmentData = examAttachmentTable
								.loadUniqueIfExistsElseInsert(new ExamAttachmentData(-1L, qualifier, attachement));

						examPartData = examPartTable.byAttachmentAndExam(examAttachmentData, examData);
						if (examPartData == null) {
							examPartData = examPartTable.insert(new ExamPartData(name));
						}
					}

					examAttachmentData.setExamPartId(examPartData.getId());
					examAttachmentTable.update(examAttachmentData);

					examPartExamTable.loadIfExistsElseInsert(new ExamPartExamData(examPartData.getId(), examData.getId()));

					final ExerciseData exerciseData = this.exerciseTable
							.loadUniqueIfExistsElseInsert(new ExerciseData(examPartData.getId(), exerciseIndex));

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
							examAttachmentData.getId(),
							additiveRectangle,
							subtractiveRectangles);

					this.exerciseAttachmentTable.loadUniqueIfExistsElseInsert(exerciseAttachment);

					{
						final List<ExerciseTagData> existingExTags = this.exerciseTagTable.byExercise(exerciseData);
						final Set<String> neededTags = Arrays.stream(tags).map(String::toLowerCase).collect(Collectors.toSet());
						final Set<Long> existingTagIds = existingExTags.stream().map(ExerciseTagData::getTagId).collect(Collectors.toSet());
						final List<TagData> existingTags = this.tagTable.loadAll(existingTagIds.stream().map(TagData::new).toList());

						final Map<Long, TagData> existingTagsById = existingTags.stream()
								.collect(Collectors.toMap(TagData::getId, Function.identity()));

						final List<ExerciseTagData> removingExTags = existingExTags.stream().filter(exTag -> {
							final TagData tagData = existingTagsById.get(exTag.getTagId());

							return tagData == null || !neededTags.contains(tagData.getName().toLowerCase());
						}).toList();

						if (!removingExTags.isEmpty()) {
							this.exerciseTagTable.deleteAll(removingExTags);
						}

						final List<TagData> neededTagDatas = neededTags.stream()
								.map(TagData::new)
								.map(this.tagTable::loadUniqueIfExists)
								.filter(Optional::isPresent)
								.map(Optional::get)
								.toList();

						final Set<String> existingTagNames = neededTagDatas.stream()
								.map(tagData -> tagData.getName().toLowerCase())
								.collect(Collectors.toSet());

						final List<TagData> missingTags = neededTags.stream()
								.filter(tagName -> !existingTagNames.contains(tagName))
								.map(TagData::new)
								.filter(c -> !c.getName().isEmpty())
								.toList();

						final List<TagData> reloadedMissingTags = missingTags.isEmpty() ? List.of()
								: allowTagCreation ? this.tagTable.insertAndReloadAll(missingTags)
								: List.of();

						final List<TagData> allNeededTagDatas = Stream.concat(neededTagDatas.stream(), reloadedMissingTags.stream())
								.toList();

						final Set<Long> removingTagIds = removingExTags.stream().map(ExerciseTagData::getTagId).collect(Collectors.toSet());

						final Set<Long> remainingTagIds = existingExTags.stream()
								.map(ExerciseTagData::getTagId)
								.filter(tagId -> !removingTagIds.contains(tagId))
								.collect(Collectors.toSet());

						final List<ExerciseTagData> addingExTags = allNeededTagDatas.stream()
								.filter(tagData -> !remainingTagIds.contains(tagData.getId()))
								.map(tagData -> new ExerciseTagData(exerciseData.getId(), tagData.getId()))
								.distinct()
								.toList();

						if (!addingExTags.isEmpty()) {
							this.exerciseTagTable.insertAndReloadAll(addingExTags);
						}
					}

					emitter.send(SseEmitter.event().name("progress").data(index + "/" + rowCount));
				}

				transaction.commit();
				emitter.send(SseEmitter.event().name("complete").data("CSV uploaded successfully"));

				emitter.complete();
			} catch (final Exception e) {
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
