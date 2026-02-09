package com.group4.projects_management.entity; /***********************************************************************
 * Module:  BaseLookup.java
 * Author:  Lenovo
 * Purpose: Defines the Class BaseLookup
 ***********************************************************************/

import jakarta.persistence.MappedSuperclass;
import lombok.Data;

/** @pdOid f18ab97a-45d6-49ce-bdf3-ccd27c6b74e0 */
@Data
@MappedSuperclass
public abstract class BaseLookup {
   /** @pdOid 92476b67-01b3-4fd2-af36-bfdcc5ddcb3b */
   private java.lang.String name;
   /** @pdOid 53e0bfc7-ea1c-45ce-8136-f2176546f824 */
   private java.lang.String description;

}