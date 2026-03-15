package com.group4.projects_management.repository; /***********************************************************************
 * Module:  ProjectRepository.java
 * Author:  Lenovo
 * Purpose: Defines the Interface ProjectRepository
 ***********************************************************************/

import com.group4.projects_management.entity.Project;
import com.group4.projects_management.repository.Base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/** @pdOid 7d78b480-cca8-40e4-936c-32299cbb1e16 */
public interface ProjectRepository extends BaseRepository<Project, Long> {

    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.members")
    List<Project> findAllWithMembers();

    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.members WHERE p.id = :projectId")
    Optional<Project> findByIdWithMembers(@Param("projectId") Long projectId);

}
