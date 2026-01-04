package org.wespeak.conversation.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotsResponse {
  private List<TimeSlotDto> timeslots;
  private Long total;
  private Boolean hasMore;
}
