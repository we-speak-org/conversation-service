package org.wespeak.lesson.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Detailed unit response with lessons.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnitDetailDto {
    private String id;
    private String title;
    private String description;
    private String courseId;
    private List<LessonSummaryDto> lessons;
}
