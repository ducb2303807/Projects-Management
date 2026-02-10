package com.group4.projects_management.controller; /***********************************************************************
 * Module:  UserController.java
 * Author:  Lenovo
 * Purpose: Defines the Class UserController
 ***********************************************************************/

import com.group4.common.dto.UserDTO;
import com.group4.common.dto.UserUpdateDTO;
import com.group4.projects_management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** @pdOid 18ed4f5e-096b-4dbb-9051-ffce97322ba3 */
@RestController
public class UserController {
   /** @pdRoleInfo migr=no name=UserService assc=association28 mult=1..1 */
   @Autowired
   private UserService userService;
   
   /** @param keyword
    * @pdOid 1c59d7f1-af61-406c-849c-3763788b4d58 */
   public ResponseEntity<List<UserDTO>> searchUsers(java.lang.String keyword) {
      // TODO: implement
      return null;
   }
   
   /** @param userId 
    * @param request
    * @pdOid aa64e632-f09c-4749-9c97-ed3720caee7c */
   public ResponseEntity<UserDTO> updateProfile(Long userId, UserUpdateDTO request) {
      // TODO: implement
      return null;
   }
   
   /** @param userId 
    * @param newPassword
    * @pdOid e42130f9-fc4a-4de3-af56-b03abb4ef5f1 */
   public ResponseEntity<Void> changePassword(Long userId, java.lang.String newPassword) {
      // TODO: implement
      return null;
   }

}