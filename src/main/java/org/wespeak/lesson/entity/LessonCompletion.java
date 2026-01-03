package org.wespeak.lesson.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * LessonCompletion - Records each time a user completes a lesson.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "lesson_completions")
@CompoundIndex(name = "idx_completion_user_lesson", def = "{'userId': 1, 'lessonId': 1, 'completedAt': -1}")
@CompoundIndex(name = "idx_completion_user_date", def = "{'userId': 1, 'completedAt': -1}")
public class LessonCompletion {

    @Id
    private String id;

    /** User ID */
    private String userId;

    /** Lesson ID */
    private String lessonId;

    /** Score obtained (0-100) */
    private Integer score;

    /** XP earned */
    private Integer xpEarned;

    /** Number of correct answers */
    private Integer correctAnswers;

    /** Total number of exercises */
    private Integer totalExercises;

    /** Time spent in seconds */
    private Integer timeSpentSeconds;

    /** Attempt number (1, 2, 3...) */
    private Integer attemptNumber;

    /** Completion timestamp */
    private Instant completedAt;
}
