package com.group4.common.dto;

import lombok.*;

import java.time.LocalDateTime;

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
   private Metadata metadata;

   @Data
   public static class Metadata {
      private String inviterName;
      private String projectName;
      private Long projectId;
      private String roleName;
      private String responseAction;
   }
}
