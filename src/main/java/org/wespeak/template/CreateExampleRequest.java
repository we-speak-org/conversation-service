package org.wespeak.template;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create an example")
public class CreateExampleRequest {

  @NotBlank(message = "Code is required")
  @Size(min = 2, max = 50, message = "Code must be between 2 and 50 characters")
  @Schema(description = "Unique code", example = "EXAMPLE_001")
  private String code;

  @NotBlank(message = "Name is required")
  @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
  @Schema(description = "Display name", example = "My Example")
  private String name;

  @Size(max = 500, message = "Description cannot exceed 500 characters")
  @Schema(description = "Optional description", example = "This is an example")
  private String description;
}
