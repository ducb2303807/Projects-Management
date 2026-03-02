package com.group4.projects_management.service.base; /***********************************************************************
 * Module:  BaseServiceImpl.java
 * Author:  Lenovo
 * Purpose: Defines the Class BaseServiceImpl
 ***********************************************************************/

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** @pdOid cd213b67-859b-4b3d-a4cb-cac9eacbbd18 */
public class BaseServiceImpl <T,ID> implements BaseService<T,ID> {
   /** @pdOid 3c9e246f-bfad-442a-8e68-1a86a2466ce7 */
   protected JpaRepository<T, ID> repository;

   public BaseServiceImpl(JpaRepository<T, ID> repository) {
      this.repository = repository;
   }
   
   /** @param id
    * @pdOid 97c15fb5-9a25-46c1-b458-17e3ed1645b7 */
   public T getByIdOrThrow(ID id) {
      // TODO: implement
      return null;
   }

   @Override
   public Optional<T> findById(ID id) {
      return Optional.empty();
   }

   @Override
   public List<T> findAll() {
      return List.of();
   }

   @Override
   public void deleteById(ID id) {

   }

   @Override
   public boolean existsById(ID id) {
      return false;
   }
}