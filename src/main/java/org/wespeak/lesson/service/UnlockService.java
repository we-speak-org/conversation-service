package org.wespeak.lesson.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wespeak.lesson.entity.Lesson;
import org.wespeak.lesson.entity.LessonCompletion;
import org.wespeak.lesson.entity.Unit;
import org.wespeak.lesson.repository.LessonCompletionRepository;
import org.wespeak.lesson.repository.LessonRepository;
import org.wespeak.lesson.repository.UnitRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service handling unlock logic for courses, units, and lessons.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UnlockService {

    private static final int UNLOCK_THRESHOLD_SCORE = 70;

    private final UnitRepository unitRepository;
    private final LessonRepository lessonRepository;
    private final LessonCompletionRepository lessonCompletionRepository;

    /**
     * Check if a unit is unlocked for a user.
     * First unit is always unlocked.
     * Subsequent units are unlocked if all lessons in the previous unit are completed.
     */
    public boolean isUnitUnlocked(String unitId, String userId) {
        if (userId == null) {
            // For anonymous users, only first unit is shown as unlocked
            Unit unit = unitRepository.findById(unitId).orElse(null);
            return unit != null && unit.getOrder() == 1;
        }

        Unit unit = unitRepository.findById(unitId).orElse(null);
        if (unit == null) return false;

        // First unit is always unlocked
        if (unit.getOrder() == 1) return true;

        // Find the previous unit
        Unit previousUnit = unitRepository.findByCourseIdAndOrder(unit.getCourseId(), unit.getOrder() - 1);
        if (previousUnit == null) return true;

        // Check if all lessons in previous unit are completed
        return areAllLessonsCompleted(previousUnit.getId(), userId);
    }

    /**
     * Check if a lesson is unlocked for a user.
     * First lesson in first unit is always unlocked.
     * Subsequent lessons are unlocked if the previous lesson is completed with score >= 70%.
     */
    public boolean isLessonUnlocked(String lessonId, String userId) {
        if (userId == null) {
            // For anonymous users, check if it's the first lesson of first unit
            Lesson lesson = lessonRepository.findById(lessonId).orElse(null);
            if (lesson == null) return false;
            if (lesson.getOrder() == 1) {
                Unit unit = unitRepository.findById(lesson.getUnitId()).orElse(null);
                return unit != null && unit.getOrder() == 1;
            }
            return false;
        }

        Lesson lesson = lessonRepository.findById(lessonId).orElse(null);
        if (lesson == null) return false;

        Unit unit = unitRepository.findById(lesson.getUnitId()).orElse(null);
        if (unit == null) return false;

        // First lesson of first unit is always unlocked
        if (lesson.getOrder() == 1 && unit.getOrder() == 1) return true;

        // If first lesson in unit, check if previous unit is completed
        if (lesson.getOrder() == 1) {
            return isUnitUnlocked(unit.getId(), userId);
        }

        // Check if previous lesson in same unit is completed
        Lesson previousLesson = lessonRepository.findByUnitIdAndOrder(lesson.getUnitId(), lesson.getOrder() - 1);
        if (previousLesson == null) return true;

        return isLessonCompletedWithMinScore(previousLesson.getId(), userId);
    }

    /**
     * Find the next lesson to unlock after completing a lesson.
     */
    public Optional<Lesson> findNextLesson(String completedLessonId) {
        Lesson completedLesson = lessonRepository.findById(completedLessonId).orElse(null);
        if (completedLesson == null) return Optional.empty();

        // Try to find next lesson in same unit
        Lesson nextLesson = lessonRepository.findByUnitIdAndOrder(
            completedLesson.getUnitId(), completedLesson.getOrder() + 1);
        
        if (nextLesson != null) return Optional.of(nextLesson);

        // If no more lessons in unit, find first lesson of next unit
        Unit currentUnit = unitRepository.findById(completedLesson.getUnitId()).orElse(null);
        if (currentUnit == null) return Optional.empty();

        Unit nextUnit = unitRepository.findByCourseIdAndOrder(currentUnit.getCourseId(), currentUnit.getOrder() + 1);
        if (nextUnit == null) return Optional.empty();

        return Optional.ofNullable(lessonRepository.findByUnitIdAndOrder(nextUnit.getId(), 1));
    }

    /**
     * Check if all lessons in a unit are completed by a user.
     */
    public boolean areAllLessonsCompleted(String unitId, String userId) {
        List<Lesson> lessons = lessonRepository.findByUnitIdOrderByOrder(unitId);
        return lessons.stream().allMatch(lesson -> isLessonCompletedWithMinScore(lesson.getId(), userId));
    }

    /**
     * Check if a lesson is completed with at least the threshold score.
     */
    public boolean isLessonCompletedWithMinScore(String lessonId, String userId) {
        return lessonCompletionRepository.existsByUserIdAndLessonIdAndScoreGreaterThanEqual(
            userId, lessonId, UNLOCK_THRESHOLD_SCORE);
    }

    /**
     * Get information about the previous lesson (for unlock requirements).
     */
    public Optional<Lesson> getPreviousLesson(String lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElse(null);
        if (lesson == null) return Optional.empty();

        if (lesson.getOrder() > 1) {
            return Optional.ofNullable(
                lessonRepository.findByUnitIdAndOrder(lesson.getUnitId(), lesson.getOrder() - 1));
        }

        // First lesson in unit - get last lesson of previous unit
        Unit unit = unitRepository.findById(lesson.getUnitId()).orElse(null);
        if (unit == null || unit.getOrder() <= 1) return Optional.empty();

        Unit previousUnit = unitRepository.findByCourseIdAndOrder(unit.getCourseId(), unit.getOrder() - 1);
        if (previousUnit == null) return Optional.empty();

        List<Lesson> previousUnitLessons = lessonRepository.findByUnitIdOrderByOrder(previousUnit.getId());
        return previousUnitLessons.isEmpty() ? Optional.empty() : Optional.of(previousUnitLessons.get(previousUnitLessons.size() - 1));
    }
}
