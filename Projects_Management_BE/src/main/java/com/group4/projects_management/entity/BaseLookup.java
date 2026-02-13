package com.group4.projects_management.entity; /***********************************************************************
 * Module:  BaseLookup.java
 * Author:  Lenovo
 * Purpose: Defines the Class BaseLookup
 ***********************************************************************/

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.io.Serializable;

@Data
@MappedSuperclass
public abstract class BaseLookup<ID extends Serializable> {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private ID id;
   private java.lang.String name;
   private java.lang.String description;
}