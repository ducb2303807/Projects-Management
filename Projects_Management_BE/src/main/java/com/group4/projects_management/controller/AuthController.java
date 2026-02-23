package com.group4.projects_management.controller;

import com.group4.common.dto.AuthResponse;
import com.group4.common.dto.LoginRequest;
import com.group4.common.dto.UserDTO;
import com.group4.common.dto.UserRegistrationDTO;
import com.group4.projects_management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
   @Autowired
   private UserService userService;

   @PostMapping("/login")
   public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
      return ResponseEntity.ok(userService.login(request));
   }

   @PostMapping("/register")
   public ResponseEntity<UserDTO> register(@RequestBody UserRegistrationDTO request) {
      return ResponseEntity.ok(userService.register(request));
   }
}