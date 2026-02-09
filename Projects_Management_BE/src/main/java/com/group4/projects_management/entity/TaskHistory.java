/***********************************************************************
 * Module:  TaskHistory.java
 * Author:  Lenovo
 * Purpose: Defines the Class TaskHistory
 ***********************************************************************/

/** @pdOid 67fc81ff-c267-46d7-9ed9-80bf169619ae */
public class TaskHistory {
   /** @pdOid 39671e43-3521-4948-9f6d-68e1512b6258 */
   private int id;
   /** @pdOid d7ec35ae-e900-40bb-9ef5-5a217e7d552d */
   private java.lang.String columnName;
   /** @pdOid 17fdd804-15a4-47a7-90a8-d641b881b02c */
   private java.lang.String oldValue;
   /** @pdOid 6d4682f9-266e-4c75-bf9e-776e7246d7ab */
   private java.lang.String newValue;
   /** @pdOid 543f4f41-1b2e-428d-b132-57c71e75245e */
   private LocalDateTime changedAt;
   
   /** @pdRoleInfo migr=no name=ProjectMember assc=association16 mult=1..1 */
   public ProjectMember changedBy;
   /** @pdRoleInfo migr=no name=Task assc=association17 mult=1..1 side=A */
   public Task task;
   
   
   /** @pdGenerated default parent getter */
   public Task getTask() {
      return task;
   }
   
   /** @pdGenerated default parent setter
     * @param newTask */
   public void setTask(Task newTask) {
      if (this.task == null || !this.task.equals(newTask))
      {
         if (this.task != null)
         {
            Task oldTask = this.task;
            this.task = null;
            oldTask.removeHistorys(this);
         }
         if (newTask != null)
         {
            this.task = newTask;
            this.task.addHistorys(this);
         }
      }
   }

}