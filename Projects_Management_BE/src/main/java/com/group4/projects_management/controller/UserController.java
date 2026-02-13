package com.group4.projects_management.controller; /***********************************************************************
 * Module:  UserController.java
 * Author:  Lenovo
 * Purpose: Defines the Class UserController
 ***********************************************************************/

import com.group4.common.dto.*;
import com.group4.projects_management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
   @Autowired
   private UserService userService;

   @GetMapping
   public ResponseEntity<List<UserDTO>> getAllUsers() {
      return ResponseEntity.ok(userService.getAllUsers());
   }

   @PostMapping
   public ResponseEntity<UserDTO> register(@RequestBody UserRegistrationDTO request) {
      return ResponseEntity.ok(userService.register(request));
   }

   @PostMapping("/exists")
   public ResponseEntity<UserExistsResponseDTO> existsByUsername(@RequestBody UserExistsRequestDTO request) {

      var exists = new UserExistsResponseDTO(
              userService.existsByUsername(request.getUsername()),
              userService.existsByEmail(request.getEmail())
      );
      return ResponseEntity.ok(exists);
   }

   @GetMapping("/search")
   public ResponseEntity<List<UserDTO>> searchUsers(
           @RequestParam String keyword) {
      return ResponseEntity.ok(userService.searchUsers(keyword));
   }

   @PutMapping("/{userId}")
   public ResponseEntity<UserDTO> updateProfile(
           @PathVariable Long userId,
           @RequestBody UserUpdateDTO request) {
      return ResponseEntity.ok(
              userService.updateProfile(userId, request)
      );
   }


   public ResponseEntity<Void> changePassword(Long userId, java.lang.String newPassword) {
      // TODO: implement
      return null;
   }

}