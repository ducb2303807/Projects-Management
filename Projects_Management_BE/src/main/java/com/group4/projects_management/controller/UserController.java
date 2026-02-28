package com.group4.projects_management.controller; /***********************************************************************
 * Module:  UserController.java
 * Author:  Lenovo
 * Purpose: Defines the Class UserController
 ***********************************************************************/

import com.group4.common.dto.*;
import com.group4.projects_management.service.ProjectService;
import com.group4.projects_management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
   @Autowired
   private UserService userService;
   @Autowired
   private ProjectService projectService;

   @Operation(summary = "Lấy tất cả thông tin của người dùng")
   @GetMapping
   public ResponseEntity<List<UserDTO>> getAllUsers() {
      return ResponseEntity.ok(userService.getAllUsers());
   }

//   @Operation(summary = "Tạo người dùng mới trong hệ thống")
//   @PostMapping
//   public ResponseEntity<UserDTO> register(@Valid @RequestBody UserRegistrationDTO request) {
//      return ResponseEntity.ok(userService.register(request));
//   }

   @Operation(summary = "Lấy tất cả project của người dùng")
   @GetMapping("/{userId}/projects")
   public ResponseEntity<List<ProjectResponseDTO>> getProjectsByUserId(@PathVariable Long userId) {
      return ResponseEntity.ok(projectService.getProjectsByUserId(userId));
   }

   @Operation(summary = "Kiểm tra tồn tại của username/email trong hệ thống")
   @PostMapping("/exists")
   public ResponseEntity<UserExistsResponseDTO> existsByUsername(@Valid @RequestBody UserExistsRequestDTO request) {

      var exists = new UserExistsResponseDTO(
              userService.existsByUsername(request.getUsername()),
              userService.existsByEmail(request.getEmail())
      );
      return ResponseEntity.ok(exists);
   }

   @Operation(summary = "Tìm kiếm người dùng",
           description = "Tìm kiếm dựa trên username hoặc email của hệ thống")
   @GetMapping("/search")
   public ResponseEntity<List<UserDTO>> searchUsers(
           @RequestParam String keyword) {
      return ResponseEntity.ok(userService.searchUsers(keyword));
   }

   @Operation(summary = "Thay đổi thông tin cá nhân của người dùng",
           description = "Thay đổi các thông tin cơ bản fullName, email, address")
   @PatchMapping("/{userId}")
   public ResponseEntity<UserDTO> updateProfile(
           @PathVariable Long userId,
           @Valid @RequestBody UserUpdateDTO request) {
      return ResponseEntity.ok(
              userService.updateProfile(userId, request)
      );
   }


   @Operation(summary = "Thay đổi mật khẩu của người dùng")
   @PatchMapping("/{userId}/change-password")
   public ResponseEntity<Void> changePassword(
           @PathVariable Long userId,
           @RequestParam String newPassword) {
      userService.changePassword(userId, newPassword);
      return ResponseEntity.ok().build();
   }

}