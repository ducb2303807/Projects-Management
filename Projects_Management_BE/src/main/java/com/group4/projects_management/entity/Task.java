/***********************************************************************
 * Module:  Task.java
 * Author:  Lenovo
 * Purpose: Defines the Class Task
 ***********************************************************************/

/** @pdOid f521481c-a646-4bde-bb82-baf28d561b0f */
public class Task {
   /** @pdOid c19f764e-b01e-4035-b8e4-fc859bd0c93b */
   private Long id;
   /** @pdOid 36d9b294-70bd-4088-85c9-9387cdfe5c18 */
   private java.lang.String name;
   /** @pdOid 25c2d70b-15dc-44c0-b482-ae4fe639c31a */
   private java.lang.String description;
   /** @pdOid 5ed148b6-5071-447e-8a13-e7f6c42e5bfd */
   private LocalDateTime deadline;
   
   /** @pdRoleInfo migr=no name=Priority assc=association8 mult=1..1 */
   public Priority priority;
   /** @pdRoleInfo migr=no name=TaskHistory assc=association17 coll=java.util.Collection impl=java.util.HashSet mult=0..* type=Composition */
   public java.util.Collection<TaskHistory> historys;
   /** @pdRoleInfo migr=no name=TaskStatus assc=association20 mult=1..1 */
   public TaskStatus taskStatus;
   /** @pdRoleInfo migr=no name=Comment assc=association21 coll=java.util.Collection impl=java.util.HashSet mult=0..* type=Composition */
   public java.util.Collection<Comment> comments;
   /** @pdRoleInfo migr=no name=Project assc=association6 mult=1..1 side=A */
   public Project project;
   
   /** @pdOid 858eb2b9-cb60-4c67-8e3b-85ffe23871d0 */
   public boolean isOverdue() {
      // TODO: implement
      return false;
   }
   
   /** @pdOid 66c26773-7b21-443a-87c5-2a41db972b6f */
   public int getMemberCount() {
      // TODO: implement
      return 0;
   }
   
   /** @pdOid 8f5c84cb-6114-4c05-98c0-ef242a913a53 */
   public boolean isUrgent() {
      // TODO: implement
      return false;
   }
   
   /** @pdOid 7c19221e-df9e-48fc-bf15-7d61911428da */
   public long getRemainingDays() {
      // TODO: implement
      return 0;
   }
   
   /** @param projectMember
    * @pdOid 056f8fe5-2b76-4b09-8667-82e11f2dabf9 */
   public void addAssignee(ProjectMember projectMember) {
      // TODO: implement
   }
   
   /** @param newStatus
    * @pdOid 734b1e10-6754-46c4-b925-adb8cc118112 */
   public boolean canUpdateStatus(TaskStatus newStatus) {
      // TODO: implement
      return false;
   }
   
   /** @param comment
    * @pdOid bc0096f3-024b-4369-8db1-174e4f6c6c54 */
   public void addComment(Comment comment) {
      // TODO: implement
   }
   
   
   /** @pdGenerated default getter */
   public java.util.Collection<TaskHistory> getHistorys() {
      if (historys == null)
         historys = new java.util.HashSet<TaskHistory>();
      return historys;
   }
   
   /** @pdGenerated default iterator getter */
   public java.util.Iterator getIteratorHistorys() {
      if (historys == null)
         historys = new java.util.HashSet<TaskHistory>();
      return historys.iterator();
   }
   
   /** @pdGenerated default setter
     * @param newHistorys */
   public void setHistorys(java.util.Collection<TaskHistory> newHistorys) {
      removeAllHistorys();
      for (java.util.Iterator iter = newHistorys.iterator(); iter.hasNext();)
         addHistorys((TaskHistory)iter.next());
   }
   
   /** @pdGenerated default add
     * @param newTaskHistory */
   public void addHistorys(TaskHistory newTaskHistory) {
      if (newTaskHistory == null)
         return;
      if (this.historys == null)
         this.historys = new java.util.HashSet<TaskHistory>();
      if (!this.historys.contains(newTaskHistory))
      {
         this.historys.add(newTaskHistory);
         newTaskHistory.setTask(this);      
      }
   }
   
   /** @pdGenerated default remove
     * @param oldTaskHistory */
   public void removeHistorys(TaskHistory oldTaskHistory) {
      if (oldTaskHistory == null)
         return;
      if (this.historys != null)
         if (this.historys.contains(oldTaskHistory))
         {
            this.historys.remove(oldTaskHistory);
            oldTaskHistory.setTask((Task)null);
         }
   }
   
   /** @pdGenerated default removeAll */
   public void removeAllHistorys() {
      if (historys != null)
      {
         TaskHistory oldTaskHistory;
         for (java.util.Iterator iter = getIteratorHistorys(); iter.hasNext();)
         {
            oldTaskHistory = (TaskHistory)iter.next();
            iter.remove();
            oldTaskHistory.setTask((Task)null);
         }
      }
   }
   /** @pdGenerated default getter */
   public java.util.Collection<Comment> getComments() {
      if (comments == null)
         comments = new java.util.HashSet<Comment>();
      return comments;
   }
   
   /** @pdGenerated default iterator getter */
   public java.util.Iterator getIteratorComments() {
      if (comments == null)
         comments = new java.util.HashSet<Comment>();
      return comments.iterator();
   }
   
   /** @pdGenerated default setter
     * @param newComments */
   public void setComments(java.util.Collection<Comment> newComments) {
      removeAllComments();
      for (java.util.Iterator iter = newComments.iterator(); iter.hasNext();)
         addComments((Comment)iter.next());
   }
   
   /** @pdGenerated default add
     * @param newComment */
   public void addComments(Comment newComment) {
      if (newComment == null)
         return;
      if (this.comments == null)
         this.comments = new java.util.HashSet<Comment>();
      if (!this.comments.contains(newComment))
      {
         this.comments.add(newComment);
         newComment.setTask(this);      
      }
   }
   
   /** @pdGenerated default remove
     * @param oldComment */
   public void removeComments(Comment oldComment) {
      if (oldComment == null)
         return;
      if (this.comments != null)
         if (this.comments.contains(oldComment))
         {
            this.comments.remove(oldComment);
            oldComment.setTask((Task)null);
         }
   }
   
   /** @pdGenerated default removeAll */
   public void removeAllComments() {
      if (comments != null)
      {
         Comment oldComment;
         for (java.util.Iterator iter = getIteratorComments(); iter.hasNext();)
         {
            oldComment = (Comment)iter.next();
            iter.remove();
            oldComment.setTask((Task)null);
         }
      }
   }
   /** @pdGenerated default parent getter */
   public Project getProject() {
      return project;
   }
   
   /** @pdGenerated default parent setter
     * @param newProject */
   public void setProject(Project newProject) {
      if (this.project == null || !this.project.equals(newProject))
      {
         if (this.project != null)
         {
            Project oldProject = this.project;
            this.project = null;
            oldProject.removeTasks(this);
         }
         if (newProject != null)
         {
            this.project = newProject;
            this.project.addTasks(this);
         }
      }
   }

}