package org.wespeak.lesson.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wespeak.lesson.dto.*;
import org.wespeak.lesson.entity.Course;
import org.wespeak.lesson.entity.Lesson;
import org.wespeak.lesson.entity.Unit;
import org.wespeak.lesson.exception.ResourceNotFoundException;
import org.wespeak.lesson.repository.CourseRepository;
import org.wespeak.lesson.repository.LessonRepository;
import org.wespeak.lesson.repository.UnitRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UnitRepository unitRepository;
    private final LessonRepository lessonRepository;
    private final UnlockService unlockService;

    /**
     * Find all published courses for a language.
     */
    public CoursesResponse findCourses(String languageCode, String level) {
        List<Course> courses;
        if (level != null && !level.isBlank()) {
            courses = courseRepository.findByTargetLanguageCodeAndLevelAndIsPublishedOrderByOrder(
                languageCode, level, true);
        } else {
            courses = courseRepository.findByTargetLanguageCodeAndIsPublishedOrderByOrder(
                languageCode, true);
        }

        List<CourseDto> courseDtos = courses.stream()
            .map(this::toCourseDto)
            .collect(Collectors.toList());

        return CoursesResponse.builder().courses(courseDtos).build();
    }

    /**
     * Get course details with units.
     */
    public CourseDetailDto findCourseById(String courseId, String userId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> ResourceNotFoundException.courseNotFound(courseId));

        List<Unit> units = unitRepository.findByCourseIdOrderByOrder(courseId);
        
        List<UnitSummaryDto> unitDtos = units.stream()
            .map(unit -> toUnitSummaryDto(unit, userId))
            .collect(Collectors.toList());

        return CourseDetailDto.builder()
            .id(course.getId())
            .title(course.getTitle())
            .level(course.getLevel())
            .description(course.getDescription())
            .imageUrl(course.getImageUrl())
            .estimatedHours(course.getEstimatedHours())
            .units(unitDtos)
            .build();
    }

    private CourseDto toCourseDto(Course course) {
        long totalUnits = unitRepository.countByCourseId(course.getId());
        List<Unit> units = unitRepository.findByCourseIdOrderByOrder(course.getId());
        long totalLessons = units.stream()
            .mapToLong(unit -> lessonRepository.countByUnitId(unit.getId()))
            .sum();

        return CourseDto.builder()
            .id(course.getId())
            .title(course.getTitle())
            .level(course.getLevel())
            .description(course.getDescription())
            .imageUrl(course.getImageUrl())
            .order(course.getOrder())
            .requiredXP(course.getRequiredXP())
            .estimatedHours(course.getEstimatedHours())
            .totalUnits((int) totalUnits)
            .totalLessons((int) totalLessons)
            .build();
    }

    private UnitSummaryDto toUnitSummaryDto(Unit unit, String userId) {
        long totalLessons = lessonRepository.countByUnitId(unit.getId());
        boolean isUnlocked = unlockService.isUnitUnlocked(unit.getId(), userId);

        return UnitSummaryDto.builder()
            .id(unit.getId())
            .title(unit.getTitle())
            .order(unit.getOrder())
            .totalLessons((int) totalLessons)
            .isUnlocked(isUnlocked)
            .build();
    }
}
