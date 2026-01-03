package org.wespeak.lesson.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * List of unlocked lessons for a user.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnlockedLessonsDto {
    private List<UnlockedLessonItem> unlockedLessons;
    private NextToUnlockDto nextToUnlock;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UnlockedLessonItem {
        private String lessonId;
        private String lessonTitle;
        private Boolean isCompleted;
        private Integer bestScore;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NextToUnlockDto {
        private String lessonId;
        private String lessonTitle;
        private String requirement;
    }
}
