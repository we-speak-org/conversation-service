package org.wespeak.lesson.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnitSummaryDto {
    private String id;
    private String title;
    private Integer order;
    private Integer totalLessons;
    private Boolean isUnlocked;
}
