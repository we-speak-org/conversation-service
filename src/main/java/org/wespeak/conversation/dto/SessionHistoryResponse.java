package org.wespeak.conversation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionHistoryResponse {
    private List<SessionHistoryItem> sessions;
    private Long total;
    private Boolean hasMore;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionHistoryItem {
        private String sessionId;
        private String targetLanguageCode;
        private String level;
        private java.time.Instant joinedAt;
        private java.time.Instant leftAt;
        private Integer participantCount;
    }
}
