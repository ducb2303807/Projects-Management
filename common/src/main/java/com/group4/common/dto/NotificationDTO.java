package com.group4.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
   private Long id;
   private String title;
   private String type;
   private Long referenceId;
   private LocalDateTime createdDate;
   private boolean isRead;
   private Map<String, Object> metadata;
}
