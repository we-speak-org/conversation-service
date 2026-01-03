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
 * Unit entity - A thematic unit containing multiple lessons.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "units")
@CompoundIndex(name = "idx_unit_course_order", def = "{'courseId': 1, 'order': 1}")
public class Unit {

    @Id
    private String id;

    /** Reference to the parent Course */
    private String courseId;

    /** Unit title */
    private String title;

    /** Unit description */
    private String description;

    /** Unit image URL */
    private String imageUrl;

    /** Position within the course (1, 2, 3...) */
    private Integer order;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
