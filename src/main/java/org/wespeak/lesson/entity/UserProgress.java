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
 * UserProgress - Tracks a user's progress for a specific language.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_progress")
@CompoundIndex(name = "idx_progress_user_lang", def = "{'userId': 1, 'targetLanguageCode': 1}", unique = true)
public class UserProgress {

    @Id
    private String id;

    /** User ID (from auth-service) */
    private String userId;

    /** Target language code */
    private String targetLanguageCode;

    /** Current course being studied */
    private String currentCourseId;

    /** Current unit being studied */
    private String currentUnitId;

    /** Current lesson being studied */
    private String currentLessonId;

    /** Total number of completed lessons */
    @Builder.Default
    private Integer lessonsCompleted = 0;

    /** Average score (0-100) */
    @Builder.Default
    private Integer averageScore = 0;

    /** Total time spent in minutes */
    @Builder.Default
    private Integer totalTimeMinutes = 0;

    /** Last activity timestamp */
    private Instant lastActivityAt;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
