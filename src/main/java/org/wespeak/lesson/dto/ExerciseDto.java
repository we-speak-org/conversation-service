package org.wespeak.lesson.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wespeak.lesson.entity.Exercise;

import java.util.Map;

/**
 * Exercise DTO - does NOT include correctAnswer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDto {
    private String id;
    private Exercise.ExerciseType type;
    private Integer order;
    private String question;
    private String hint;
    private String audioUrl;
    private String imageUrl;
    private Map<String, Object> content;
    private Integer points;
}
