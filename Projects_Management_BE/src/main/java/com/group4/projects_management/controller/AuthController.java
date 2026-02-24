package com.group4.projects_management.controller;

import com.group4.common.dto.AuthResponse;
import com.group4.common.dto.LoginRequest;
import com.group4.common.dto.UserDTO;
import com.group4.common.dto.UserRegistrationDTO;
import com.group4.projects_management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
   @Autowired
   private UserService userService;

   @Operation(
           summary = "Đăng nhập vào hệ thống",
           description = "Kiểm tra thông tin đăng nhập. Trả về token nếu thành công. " +
                   "Lỗi trả về bao gồm: USER_NOT_FOUND, ACCOUNT_LOCKED, INVALID_PASSWORD."
   )
   @ApiResponses(value = {
           @ApiResponse(responseCode = "200", description = "Đăng nhập thành công"),
           @ApiResponse(responseCode = "400", description = "Lỗi nghiệp vụ (Sai MK, Khóa tài khoản, Không tìm thấy User)",
                   content = @Content(schema = @Schema(implementation = com.group4.common.dto.ErrorResponse.class))),
           @ApiResponse(responseCode = "500", description = "Lỗi hệ thống không xác định")
   })
   @PostMapping("/login")
   public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
      return ResponseEntity.ok(userService.login(request));
   }

   @Operation(
           summary = "Đăng ký thành viên mới",
           description = "Tạo tài khoản người dùng mới. Kiểm tra trùng lặp Email/Username."
   )
   @ApiResponses(value = {
           @ApiResponse(responseCode = "200", description = "Đăng ký thành công"),
           @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc lỗi nghiệp vụ (USERNAME_ALREADY_EXISTS, EMAIL_ALREADY_EXISTS)",
                   content = @Content(schema = @Schema(implementation = com.group4.common.dto.ErrorResponse.class))),
           @ApiResponse(responseCode = "500", description = "Lỗi hệ thống",
                   content = @Content(schema = @Schema(implementation = com.group4.common.dto.ErrorResponse.class)))
   })
   @PostMapping("/register")
   public ResponseEntity<UserDTO> register(@RequestBody UserRegistrationDTO request) {
      return ResponseEntity.ok(userService.register(request));
   }
}