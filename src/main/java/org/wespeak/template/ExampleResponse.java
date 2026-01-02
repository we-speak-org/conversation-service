package org.wespeak.template;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Example response")
public class ExampleResponse {

  @Schema(description = "Unique identifier", example = "507f1f77bcf86cd799439011")
  private String id;

  @Schema(description = "Unique code", example = "EXAMPLE_001")
  private String code;

  @Schema(description = "Display name", example = "My Example")
  private String name;

  @Schema(description = "Description", example = "This is an example")
  private String description;

  @Schema(description = "Active status", example = "true")
  private boolean active;

  @Schema(description = "Creation timestamp")
  private Instant createdAt;

  @Schema(description = "Last update timestamp")
  private Instant updatedAt;
}
