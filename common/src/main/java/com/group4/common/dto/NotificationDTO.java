package com.group4.common.dto; /***********************************************************************
 * Module:  NotificationDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class NotificationDTO
 ***********************************************************************/

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
   private Long id;
   private String title;
   private java.lang.String type;
   private Long referenceId;
   private LocalDateTime createdDate;
   private boolean isRead;
   @JsonRawValue
   private String metadata;
}