package com.group4.projects_management.controller; /***********************************************************************
 * Module:  AuthController.java
 * Author:  Lenovo
 * Purpose: Defines the Class AuthController
 ***********************************************************************/

import com.group4.common.dto.AuthResponse;
import com.group4.common.dto.LoginRequest;
import com.group4.common.dto.UserDTO;
import com.group4.common.dto.UserRegistrationDTO;
import com.group4.projects_management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** @pdOid 0607231e-fb06-4382-a69e-19c5a68f677d */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
   /** @pdRoleInfo migr=no name=UserService assc=association27 mult=1..1 */
   @Autowired
   private UserService userService;
   
   /** @param request
    * @pdOid 91c91f2f-b3dc-4edd-a7ac-489cf42393ff */
   @PostMapping("/login")
   public ResponseEntity<AuthResponse> login(LoginRequest request) {
      // TODO: implement
      return null;
   }
   
   /** @param request
    * @pdOid 7b5656f9-ec7d-4f11-8ccd-1696ae8ef5e3 */
   @PostMapping("/register")
   public ResponseEntity<UserDTO> register(UserRegistrationDTO request) {
      // TODO: implement
      return null;
   }

}