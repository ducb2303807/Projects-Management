package com.group4.projects_management.repository; /***********************************************************************
 * Module:  ProjectMemberRepository.java
 * Author:  Lenovo
 * Purpose: Defines the Interface ProjectMemberRepository
 ***********************************************************************/

import com.group4.projects_management.entity.ProjectMember;
import com.group4.projects_management.repository.Base.BaseRepository;

import java.util.List;

/** @pdOid 2c6d799b-fc02-4a09-a1f3-21ac987720f0 */
public interface ProjectMemberRepository extends BaseRepository<ProjectMember, Long> {

    List<ProjectMember> findByUser_IdAndLeftAtIsNullAndProjectMemberStatus_SystemCode(Long userId, String status);
}