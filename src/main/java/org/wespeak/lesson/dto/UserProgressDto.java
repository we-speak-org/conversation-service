package org.wespeak.lesson.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * User progress for a language.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProgressDto {
    private String userId;
    private String targetLanguageCode;
    private CourseRefDto currentCourse;
    private UnitRefDto currentUnit;
    private LessonRefDto currentLesson;
    private StatsDto stats;
    private Instant lastActivityAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseRefDto {
        private String id;
        private String title;
        private String level;
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
    public static class StatsDto {
        private Integer lessonsCompleted;
        private Integer averageScore;
        private Integer totalTimeMinutes;
    }
}
