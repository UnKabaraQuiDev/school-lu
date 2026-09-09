package lu.kbra.school_lu.endpoints;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lu.kbra.school_lu.data.ExamAttachmentType;
import lu.kbra.school_lu.data.ExamSeason;
import lu.kbra.school_lu.data.ExamType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public final class ReturnTypes {

	public record NextExercise(
			boolean withSolutionOnly,
			@NotEmpty Map<@NotBlank @NotNull String, @NotEmpty Set<@NotBlank @NotNull String>> subjects,
			Set<@NotBlank @NotNull String> requiredTags,
			boolean excludeSuccess,
			@Valid YearRange yearRange) {
	}

	public record Exam(String section, String subject, int year, ExamSeason season, ExamType subtype, String name) {
	}

	public record Exercise(Exam exam, long id, int exerciseIndex, List<ExerciseAttachment> attachments, List<Tag> tags) {
	}

	public record ExerciseAttachment(ExamAttachmentType qualifier, String location) {
	}

	public record Tag(int color, String name) {
	}

	public record ErrorBody(String message, Object obj) {
	}

	public record TagBottleneck(String name, int count, int negativeCount, double negativePercentage) {
	}

	public record YearRange(@Positive int from, @Positive int to) {

	}

	private ReturnTypes() {
	}

}
