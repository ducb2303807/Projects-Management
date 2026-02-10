package com.group4.projects_management.service; /***********************************************************************
 * Module:  UserService.java
 * Author:  Lenovo
 * Purpose: Defines the Interface UserService
 ***********************************************************************/

import com.group4.common.dto.*;

import java.util.List;

/** @pdOid ab27f933-c9d9-479c-afec-dc5761a6e4e3 */
public interface UserService {
   /** @param request
    * @pdOid b53e95d0-0b93-4663-93a0-bace5717fd9b */
   AuthResponse login(LoginRequest request);
   /** @param dto
    * @pdOid 54bec4b6-a897-4509-8f8e-1eb816a57918 */
   UserDTO register(UserRegistrationDTO dto);
   /** @param userId 
    * @param dto
    * @pdOid 92284f37-5992-4a20-977e-6e00f0dd6d8e */
   UserDTO updateProfile(Long userId, UserUpdateDTO dto);
   /** @param userId 
    * @param newPassword
    * @pdOid 5d1f89a3-240e-4cc9-9600-e39c2761397a */
   void changePassword(Long userId, String oldPassword, java.lang.String newPassword);
   /** @param keyword
    * @pdOid 231341be-4940-4c16-8e89-798ef0cd1d35 */
   List<UserDTO> searchUsers(java.lang.String keyword);

}