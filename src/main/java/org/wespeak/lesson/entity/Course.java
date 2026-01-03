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

/**
 * Course entity - A complete course for a CEFR level (A1, A2, B1, B2, C1, C2).
 * Contains multiple Units.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "courses")
@CompoundIndex(name = "idx_course_lang_level", def = "{'targetLanguageCode': 1, 'level': 1, 'isPublished': 1}")
@CompoundIndex(name = "idx_course_lang_order", def = "{'targetLanguageCode': 1, 'order': 1}")
public class Course {

    @Id
    private String id;

    /** Target language code (e.g., "en", "fr", "es") */
    private String targetLanguageCode;

    /** CEFR level: A1, A2, B1, B2, C1, C2 */
    private String level;

    /** Course title */
    private String title;

    /** Course description */
    private String description;

    /** Course image URL */
    private String imageUrl;

    /** Position in the curriculum (1, 2, 3...) */
    private Integer order;

    /** XP required to unlock this course (0 for the first course) */
    @Builder.Default
    private Integer requiredXP = 0;

    /** Estimated duration in hours */
    private Integer estimatedHours;

    /** Whether the course is visible to users */
    @Builder.Default
    private Boolean isPublished = false;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
