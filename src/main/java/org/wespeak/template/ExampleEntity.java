package org.wespeak.template;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/** Example entity - Replace with your own domain model */
@Document(collection = "examples")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExampleEntity {

  @Id private String id;

  @Indexed(unique = true)
  private String code;

  private String name;

  private String description;

  private boolean active;

  @CreatedDate private Instant createdAt;

  @LastModifiedDate private Instant updatedAt;
}
