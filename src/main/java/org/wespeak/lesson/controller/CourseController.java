package org.wespeak.lesson.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wespeak.lesson.dto.CourseDetailDto;
import org.wespeak.lesson.dto.CoursesResponse;
import org.wespeak.lesson.service.CourseService;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Course management endpoints")
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    @Operation(summary = "List courses", description = "Get all published courses for a language")
    public ResponseEntity<CoursesResponse> getCourses(
            @Parameter(description = "Language code (e.g., 'en', 'fr', 'es')", required = true)
            @RequestParam String language,
            @Parameter(description = "CEFR level filter (A1, A2, B1, B2, C1, C2)")
            @RequestParam(required = false) String level) {
        
        CoursesResponse response = courseService.findCourses(language, level);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{courseId}")
    @Operation(summary = "Get course details", description = "Get a course with its units")
    public ResponseEntity<CourseDetailDto> getCourse(
            @PathVariable String courseId,
            Principal principal) {
        
        String userId = principal != null ? principal.getName() : null;
        CourseDetailDto response = courseService.findCourseById(courseId, userId);
        return ResponseEntity.ok(response);
    }
}
