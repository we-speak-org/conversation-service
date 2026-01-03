package org.wespeak.lesson.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wespeak.lesson.dto.LessonSummaryDto;
import org.wespeak.lesson.dto.UnitDetailDto;
import org.wespeak.lesson.entity.Lesson;
import org.wespeak.lesson.entity.LessonCompletion;
import org.wespeak.lesson.entity.Unit;
import org.wespeak.lesson.exception.ResourceNotFoundException;
import org.wespeak.lesson.repository.LessonCompletionRepository;
import org.wespeak.lesson.repository.LessonRepository;
import org.wespeak.lesson.repository.UnitRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnitService {

    private final UnitRepository unitRepository;
    private final LessonRepository lessonRepository;
    private final LessonCompletionRepository lessonCompletionRepository;
    private final UnlockService unlockService;

    /**
     * Get unit details with lessons and user progress.
     */
    public UnitDetailDto findUnitById(String unitId, String userId) {
        Unit unit = unitRepository.findById(unitId)
            .orElseThrow(() -> ResourceNotFoundException.unitNotFound(unitId));

        List<Lesson> lessons = lessonRepository.findByUnitIdOrderByOrder(unitId);
        
        // Get user's completion data for all lessons in this unit
        Map<String, LessonCompletion> bestCompletions = getBestCompletionsMap(userId, lessons);

        List<LessonSummaryDto> lessonDtos = lessons.stream()
            .map(lesson -> toLessonSummaryDto(lesson, userId, bestCompletions))
            .collect(Collectors.toList());

        return UnitDetailDto.builder()
            .id(unit.getId())
            .title(unit.getTitle())
            .description(unit.getDescription())
            .courseId(unit.getCourseId())
            .lessons(lessonDtos)
            .build();
    }

    private Map<String, LessonCompletion> getBestCompletionsMap(String userId, List<Lesson> lessons) {
        if (userId == null) {
            return Map.of();
        }
        List<String> lessonIds = lessons.stream().map(Lesson::getId).collect(Collectors.toList());
        List<LessonCompletion> completions = lessonCompletionRepository.findByUserIdAndLessonIdIn(userId, lessonIds);
        
        // Group by lessonId and get the best score for each
        return completions.stream()
            .collect(Collectors.toMap(
                LessonCompletion::getLessonId,
                c -> c,
                (c1, c2) -> c1.getScore() >= c2.getScore() ? c1 : c2
            ));
    }

    private LessonSummaryDto toLessonSummaryDto(Lesson lesson, String userId, Map<String, LessonCompletion> bestCompletions) {
        boolean isUnlocked = unlockService.isLessonUnlocked(lesson.getId(), userId);
        LessonCompletion bestCompletion = bestCompletions.get(lesson.getId());
        boolean isCompleted = bestCompletion != null && bestCompletion.getScore() >= 70;

        return LessonSummaryDto.builder()
            .id(lesson.getId())
            .title(lesson.getTitle())
            .type(lesson.getType())
            .order(lesson.getOrder())
            .estimatedMinutes(lesson.getEstimatedMinutes())
            .xpReward(lesson.getXpReward())
            .isUnlocked(isUnlocked)
            .isCompleted(isCompleted)
            .bestScore(bestCompletion != null ? bestCompletion.getScore() : null)
            .build();
    }
}
