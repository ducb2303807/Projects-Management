/***********************************************************************
 * Module:  ProjectMember.java
 * Author:  Lenovo
 * Purpose: Defines the Class ProjectMember
 ***********************************************************************/

/** @pdOid b611216c-16aa-4354-9881-f75a46baba4f */
public class ProjectMember {
   /** @pdOid 9aaaddb9-1a6f-4acc-8946-b43e089e6573 */
   private Long id;
   /** @pdOid b74414a0-0f73-4748-8495-71b9ac5c46e7 */
   private LocalDateTime joinAt;
   /** @pdOid 504436bf-88e0-4370-ac97-2784889f884a */
   private LocalDateTime leftAt;
   
   /** @pdRoleInfo migr=no name=ProjectMember assc=invitedBy mult=1..1 */
   public ProjectMember invitedBy;
   /** @pdRoleInfo migr=no name=ProjectMemberStatus assc=association10 mult=1..1 */
   public ProjectMemberStatus projectMemberStatus;
   /** @pdRoleInfo migr=no name=User assc=association11 mult=1..1 */
   public User user;
   /** @pdRoleInfo migr=no name=ProjectRole assc=association12 mult=1..1 */
   public ProjectRole projectRole;
   /** @pdRoleInfo migr=no name=Project assc=association9 mult=1..1 side=A */
   public Project project;
   
   
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
            oldProject.removeMembers(this);
         }
         if (newProject != null)
         {
            this.project = newProject;
            this.project.addMembers(this);
         }
      }
   }

}