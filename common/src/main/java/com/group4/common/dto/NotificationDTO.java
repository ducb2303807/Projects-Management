package com.group4.common.dto; /***********************************************************************
 * Module:  NotificationDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class NotificationDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** @pdOid 6168c17b-8f94-4f9d-8b6c-b8ec0eeb6542 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
   /** @pdOid c72c2589-7089-4f03-945e-fc524b87d970 */
   private Long notificationId;
   /** @pdOid 6d647f9f-c60f-4fdd-b122-7edd53c52368 */
   private Long referenceId;
   /** @pdOid b39bfc92-9ee2-45d5-9910-e78794e80b28 */
   private java.lang.String text;
   /** @pdOid 395fb085-2822-4156-be2c-d8c4961f903e */
   private java.lang.String type;
   /** @pdOid 8ed75dff-2e77-47b2-b060-4c45b6d8b3f3 */
   private LocalDateTime createAt;
   /** @pdOid 96a6205f-1705-447a-9d8a-d1f2f54e17f1 */
   private boolean isRead;

}