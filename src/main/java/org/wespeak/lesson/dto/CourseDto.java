package org.wespeak.lesson.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {
    private String id;
    private String title;
    private String level;
    private String description;
    private String imageUrl;
    private Integer order;
    private Integer requiredXP;
    private Integer estimatedHours;
    private Integer totalUnits;
    private Integer totalLessons;
}
