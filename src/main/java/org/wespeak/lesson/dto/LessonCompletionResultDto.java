package org.wespeak.lesson.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Result of completing a lesson.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonCompletionResultDto {
    private CompletionDto completion;
    private UnlockedDto unlocked;
    private ProgressStatsDto progress;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompletionDto {
        private String id;
        private String lessonId;
        private Integer score;
        private Integer xpEarned;
        private Integer attemptNumber;
        private Instant completedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UnlockedDto {
        private LessonRefDto nextLesson;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LessonRefDto {
        private String id;
        private String title;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProgressStatsDto {
        private Integer lessonsCompleted;
        private Integer averageScore;
    }
}
