package org.wespeak.lesson.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wespeak.lesson.entity.Lesson;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonSummaryDto {
    private String id;
    private String title;
    private Lesson.LessonType type;
    private Integer order;
    private Integer estimatedMinutes;
    private Integer xpReward;
    private Boolean isUnlocked;
    private Boolean isCompleted;
    private Integer bestScore;
}
