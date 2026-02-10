package com.group4.common.dto; /***********************************************************************
 * Module:  TaskHistoryDTO.java
 * Author:  Lenovo
 * Purpose: Defines the Class TaskHistoryDTO
 ***********************************************************************/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** @pdOid f1a3e9dd-d3b5-49c2-a69a-5627adedf548 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskHistoryDTO {
   /** @pdOid 4c9a7d3b-5c3e-475e-a51c-d22830eb4e5f */
   private LocalDateTime changedAt;
   /** @pdOid 7531c631-edf0-4c68-a761-849b101bce77 */
   private java.lang.String changeBy;
   /** @pdOid da7fe845-6b22-4865-8db2-21d841f51768 */
   private java.lang.String columnName;
   /** @pdOid 7dac1f8c-7b4c-4339-9f97-e4102f177041 */
   private java.lang.String oldValue;
   /** @pdOid c0e8aa82-2015-4e23-9582-4928b0775a46 */
   private java.lang.String newValue;

}