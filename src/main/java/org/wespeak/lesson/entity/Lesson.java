package org.wespeak.lesson.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Lesson entity - An individual lesson with exercises.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "lessons")
@CompoundIndex(name = "idx_lesson_unit_order", def = "{'unitId': 1, 'order': 1}")
public class Lesson {

    @Id
    private String id;

    /** Reference to the parent Unit */
    private String unitId;

    /** Lesson title */
    private String title;

    /** Lesson description */
    private String description;

    /** Lesson type: vocabulary, grammar, listening, speaking, conversation_prep */
    private LessonType type;

    /** Position within the unit (1, 2, 3...) */
    private Integer order;

    /** Estimated duration in minutes */
    private Integer estimatedMinutes;

    /** Base XP reward for completing this lesson */
    private Integer xpReward;

    /** Embedded exercises */
    @Builder.Default
    private List<Exercise> exercises = new ArrayList<>();

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public enum LessonType {
        vocabulary,
        grammar,
        listening,
        speaking,
        conversation_prep
    }
}
