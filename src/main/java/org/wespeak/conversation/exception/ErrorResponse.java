package org.wespeak.conversation.exception;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
  private ErrorBody error;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ErrorBody {
    private String code;
    private String message;
    private Map<String, Object> details;
  }

  public static ErrorResponse of(String code, String message) {
    return ErrorResponse.builder()
        .error(ErrorBody.builder().code(code).message(message).build())
        .build();
  }

  public static ErrorResponse of(String code, String message, Map<String, Object> details) {
    return ErrorResponse.builder()
        .error(ErrorBody.builder().code(code).message(message).details(details).build())
        .build();
  }
}
