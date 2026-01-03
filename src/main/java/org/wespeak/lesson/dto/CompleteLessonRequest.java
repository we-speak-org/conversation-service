package org.wespeak.lesson.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body to complete a lesson.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteLessonRequest {
    
    @NotNull
    @Min(0)
    @Max(100)
    private Integer score;
    
    @NotNull
    @Min(0)
    private Integer correctAnswers;
    
    @NotNull
    @Min(0)
    private Integer totalExercises;
    
    @NotNull
    @Min(0)
    private Integer timeSpentSeconds;
}
