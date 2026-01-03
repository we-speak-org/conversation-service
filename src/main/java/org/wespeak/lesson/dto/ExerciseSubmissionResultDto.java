package org.wespeak.lesson.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Result of submitting an answer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseSubmissionResultDto {
    private Boolean isCorrect;
    private Integer pointsEarned;
    private Map<String, Object> correctAnswer;
    private String feedback;
    private Integer attemptNumber;
}
