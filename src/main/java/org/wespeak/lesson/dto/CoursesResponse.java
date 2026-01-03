package org.wespeak.lesson.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for listing courses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoursesResponse {
    private List<CourseDto> courses;
}
