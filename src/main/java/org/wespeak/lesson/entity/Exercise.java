package org.wespeak.lesson.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Exercise - Embedded document within a Lesson.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {

    /** Unique identifier for the exercise */
    private String id;

    /** Exercise type: mcq, fill_gap, translation, listen_repeat, match_pairs, ordering */
    private ExerciseType type;

    /** Position within the lesson (1, 2, 3...) */
    private Integer order;

    /** Question or instruction */
    private String question;

    /** Optional hint for the user */
    private String hint;

    /** Audio URL (for listening exercises) */
    private String audioUrl;

    /** Image URL */
    private String imageUrl;

    /** Content specific to the exercise type (options for MCQ, sentence for fill_gap, etc.) */
    private Map<String, Object> content;

    /** Correct answer(s) */
    private Map<String, Object> correctAnswer;

    /** Points for this exercise */
    private Integer points;

    public enum ExerciseType {
        mcq,
        fill_gap,
        translation,
        listen_repeat,
        match_pairs,
        ordering
    }
}
