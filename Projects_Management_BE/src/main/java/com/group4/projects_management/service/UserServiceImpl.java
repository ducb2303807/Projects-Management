package com.group4.projects_management.service; /***********************************************************************
 * Module:  UserServiceImpl.java
 * Author:  Lenovo
 * Purpose: Defines the Class UserServiceImpl
 ***********************************************************************/

import com.group4.common.dto.*;
import com.group4.projects_management.entity.User;
import com.group4.projects_management.repository.UserRepository;
import com.group4.projects_management.service.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/** @pdOid 5c7fa502-42c5-4869-8a02-2c98dd204c57 */
@Service
public class UserServiceImpl extends BaseServiceImpl<User,Long> implements UserService {
   /** @pdRoleInfo migr=no name=UserRepository assc=association31 mult=1..1 */
   private final UserRepository userRepository;

   public UserServiceImpl(UserRepository userRepository) {
      super(userRepository);
      this.userRepository = userRepository;
   }

   @Override
   public AuthResponse login(LoginRequest request) {
      return null;
   }

   @Override
   public UserDTO register(UserRegistrationDTO dto) {
      return null;
   }

   @Override
   public UserDTO updateProfile(Long userId, UserUpdateDTO dto) {
      return null;
   }

   @Override
   public void changePassword(Long userId, String newPassword) {

   }

   @Override
   public List<UserDTO> searchUsers(String keyword) {
      return List.of();
   }
}