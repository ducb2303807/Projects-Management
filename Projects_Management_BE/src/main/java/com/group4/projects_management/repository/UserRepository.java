package com.group4.projects_management.repository; /***********************************************************************
 * Module:  UserRepository.java
 * Author:  Lenovo
 * Purpose: Defines the Interface UserRepository
 ***********************************************************************/

import com.group4.projects_management.entity.User;
import com.group4.projects_management.repository.Base.BaseRepository;

import java.util.Optional;

/** @pdOid 54381e95-8f57-4313-8d97-c7a64870d581 */
public interface UserRepository extends BaseRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}