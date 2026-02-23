package com.group4.common.dto; /***********************************************************************
 * Module:  CommentDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class CommentDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** @pdOid e802d315-5e04-4935-924f-53dae9565aeb */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO extends CommentBaseDTO {
   /** @pdOid d10389ea-3ef7-4c35-94cf-b569f3c81952 */
   private Long commentId;
   /** @pdOid b46554af-1240-41d1-a60a-93df28779490 */
   private Long parentId;
   /** @pdOid 511e571f-0b66-441b-b1f0-51a15081b836 */
   private java.lang.String userName;
   /** @pdOid 5e692eba-6733-4ef0-854d-402c112e2201 */
   private java.lang.String fullName;
   /** @pdOid d239f4e4-a523-4a92-994a-c3f689a41057 */
   private LocalDateTime createAt;

}