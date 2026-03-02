package com.group4.common.dto; /***********************************************************************
 * Module:  NotificationDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class NotificationDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
   private Long id;
   private java.lang.String text;
   private java.lang.String type;
   private Long referenceId;
   private LocalDateTime createdDate;
   private boolean isRead;
}