package com.group4.common.dto; /***********************************************************************
 * Module:  CommentDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class CommentDTO
 ***********************************************************************/

import java.time.LocalDateTime;

/** @pdOid e802d315-5e04-4935-924f-53dae9565aeb */
public class CommentDTO {
   /** @pdOid d10389ea-3ef7-4c35-94cf-b569f3c81952 */
   private Long commentId;
   /** @pdOid b46554af-1240-41d1-a60a-93df28779490 */
   private Long parentId;
   /** @pdOid 37a4561a-6bff-4fc1-a4ff-a711608c782d */
   private Long projectMemberId;
   /** @pdOid 60da45df-3042-42aa-85d9-46f2b95a4c2c */
   private java.lang.String content;
   /** @pdOid 511e571f-0b66-441b-b1f0-51a15081b836 */
   private java.lang.String userName;
   /** @pdOid d239f4e4-a523-4a92-994a-c3f689a41057 */
   private LocalDateTime createAt;

}