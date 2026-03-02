
/*==============================================================*/
/* Table: APP_ROLE                                              */
/*==============================================================*/
create table APP_ROLE
(
   APP_ROLE_ID          int not null auto_increment  comment '',
   SYSTEM_CODE          varchar(50) not null  comment '',
   SYSTEM_NAME          varchar(50) not null  comment '',
   SYSTEM_DESCRIPTION   text  comment '',
   primary key (APP_ROLE_ID)
);

/*==============================================================*/
/* Table: APP_ROLE_PERMISSION                                   */
/*==============================================================*/
create table APP_ROLE_PERMISSION
(
   PERMISSION_ID        int not null  comment '',
   APP_ROLE_ID          int not null  comment '',
   primary key (PERMISSION_ID, APP_ROLE_ID)
);

/*==============================================================*/
/* Table: NOTIFICATION                                          */
/*==============================================================*/
create table NOTIFICATION
(
   NOTIFICATION_ID      int not null auto_increment  comment '',
   NOTIFICATION_CREATED_AT timestamp not null DEFAULT CURRENT_TIMESTAMP  comment '',
   NOTIFICATION_TEXT    text not null  comment '',
   NOTIFICATION_TYPE    varchar(50) not null  comment '',
   NOTIFICATION_REFERENCE_ID int not null  comment '',
   primary key (NOTIFICATION_ID)
);

alter table NOTIFICATION comment 'notification_type là loại table nào
notification_refer';

/*==============================================================*/
/* Table: PERMISSION                                            */
/*==============================================================*/
create table PERMISSION
(
   PERMISSION_ID        int not null auto_increment  comment '',
   SYSTEM_CODE          varchar(50) not null  comment '',
   SYSTEM_NAME          varchar(50) not null  comment '',
   SYSTEM_DESCRIPTION   text  comment '',
   PERMISSION_NAME      varchar(50)  comment '',
   primary key (PERMISSION_ID)
);

/*==============================================================*/
/* Table: PLUGIN                                                */
/*==============================================================*/
create table PLUGIN
(
   PLUGIN_ID            int not null auto_increment  comment '',
   PLUGIN_NAME          varchar(100) not null  comment '',
   PLUGIN_VERSION       varchar(50) not null  comment '',
   PLUGIN_DESCRIPTION   text  comment '',
   PLUGIN_IS_ACTIVE     bool not null default TRUE comment '',
   PLUGIN_PATH          varchar(255) not null  comment '',
   PLUGIN_MAIN_CLASS    varchar(255) not null  comment '',
   primary key (PLUGIN_ID)
);

/*==============================================================*/
/* Table: PRIORITY                                              */
/*==============================================================*/
create table PRIORITY
(
   PRIORITY_ID          int not null auto_increment  comment '',
   SYSTEM_CODE          varchar(50) not null  comment '',
   SYSTEM_NAME          varchar(50) not null  comment '',
   SYSTEM_DESCRIPTION   text  comment '',
   primary key (PRIORITY_ID)
);

/*==============================================================*/
/* Table: PROJECT                                               */
/*==============================================================*/
create table PROJECT
(
   PROJECT_ID           int not null auto_increment  comment '',
   PROJECT_CREATE_BY_ID int not null  comment '',
   PROJECT_STATUS_ID    int not null  comment '',
   PROJECT_NAME         varchar(100) not null  comment '',
   PROJECT_DESCRIPTION  text  comment '',
   PROJECT_START_AT     timestamp not null  comment '',
   PROJECT_END_AT       timestamp not null  comment '',
   PROJECT_CREATED_AT   timestamp not null DEFAULT CURRENT_TIMESTAMP  comment '',
   PROJECT_UPDATE_AT    timestamp not null DEFAULT CURRENT_TIMESTAMP comment '',
   primary key (PROJECT_ID)
);

/*==============================================================*/
/* Table: PROJECT_MEMBER                                        */
/*==============================================================*/
create table PROJECT_MEMBER
(
   PROJECT_MEMBER_ID    int not null auto_increment  comment '',
   USER_ID              int not null  comment '',
   PROJECT_ID           int not null  comment '',
   PROJECT_ROLE_ID      int not null  comment '',
   PROJECT_MEMBER_STATUS_ID int not null  comment '',
   PROJECT_MEMBER_INVITE_ID int  comment '',
   PROJECT_MEMBER_JOIN_AT timestamp not null DEFAULT CURRENT_TIMESTAMP comment '',
   PROJECT_MEMBER_LEFT_AT timestamp  comment '',
   primary key (PROJECT_MEMBER_ID),
   key AK_IDENTIFIER_2 (USER_ID, PROJECT_ID)
);

/*==============================================================*/
/* Table: PROJECT_MEMBER_STATUS                                 */
/*==============================================================*/
create table PROJECT_MEMBER_STATUS
(
   PROJECT_MEMBER_STATUS_ID int not null auto_increment  comment '',
   SYSTEM_CODE          varchar(50) not null  comment '',
   SYSTEM_NAME          varchar(50) not null  comment '',
   SYSTEM_DESCRIPTION   text  comment '',
   primary key (PROJECT_MEMBER_STATUS_ID)
);

/*==============================================================*/
/* Table: PROJECT_ROLE                                          */
/*==============================================================*/
create table PROJECT_ROLE
(
   PROJECT_ROLE_ID      int not null auto_increment  comment '',
   SYSTEM_CODE          varchar(50) not null  comment '',
   SYSTEM_NAME          varchar(50) not null  comment '',
   SYSTEM_DESCRIPTION   text  comment '',
   primary key (PROJECT_ROLE_ID)
);

/*==============================================================*/
/* Table: PROJECT_ROLE_PERMISSION                               */
/*==============================================================*/
create table PROJECT_ROLE_PERMISSION
(
   PROJECT_ROLE_ID      int not null  comment '',
   PERMISSION_ID        int not null  comment '',
   primary key (PROJECT_ROLE_ID, PERMISSION_ID)
);

/*==============================================================*/
/* Table: PROJECT_STATUS                                        */
/*==============================================================*/
create table PROJECT_STATUS
(
   PROJECT_STATUS_ID    int not null auto_increment  comment '',
   SYSTEM_CODE          varchar(50) not null  comment '',
   SYSTEM_NAME          varchar(50) not null  comment '',
   SYSTEM_DESCRIPTION   text  comment '',
   primary key (PROJECT_STATUS_ID)
);

/*==============================================================*/
/* Table: TASK                                                  */
/*==============================================================*/
create table TASK
(
   TASK_ID              int not null auto_increment  comment '',
   PROJECT_ID           int not null  comment '',
   TASK_STATUS_ID       int not null  comment '',
   PRIORITY_ID          int not null  comment '',
   TASK_NAME            varchar(50) not null  comment '',
   TASK_CREATED_AT      timestamp not null DEFAULT CURRENT_TIMESTAMP comment '',
   TASK_DEADLINE        timestamp  comment '',
   TASK_DESCRIPTION     text  comment '',
   primary key (TASK_ID)
);

/*==============================================================*/
/* Table: TASK_ASSIGNMENT                                       */
/*==============================================================*/
create table TASK_ASSIGNMENT
(
   TASK_ASSIGNMENT_ID   int not null auto_increment  comment '',
   TASK_ID              int not null  comment '',
   TASK_ASSIGNEE        int not null  comment '',
   TASK_ASSIGNER        int not null  comment '',
   TASK_ASSIGNED_AT     timestamp not null DEFAULT CURRENT_TIMESTAMP  comment '',
   primary key (TASK_ASSIGNMENT_ID),
   key AK_IDENTIFIER_2 (TASK_ID, TASK_ASSIGNEE)
);

/*==============================================================*/
/* Table: TASK_COMMENT                                          */
/*==============================================================*/
create table TASK_COMMENT
(
   TASK_COMMENT_ID      int not null auto_increment  comment '',
   TASK_ID              int not null  comment '',
   PROJECT_MEMBER_ID    int not null  comment '',
   PARENT_ID            int  comment '',
   TASK_COMMENT_TEXT    text not null  comment '',
   TASK_COMMENT_CREATED_AT timestamp not null DEFAULT CURRENT_TIMESTAMP  comment '',
   primary key (TASK_COMMENT_ID)
);

/*==============================================================*/
/* Table: TASK_HISTORY                                          */
/*==============================================================*/
create table TASK_HISTORY
(
   TASK_HISTORY_ID      int not null auto_increment  comment '',
   TASK_ID              int not null  comment '',
   PROJECT_MEMBER_ID    int not null  comment '',
   TASK_HISTORY_COLUMN_NAME varchar(50) not null  comment '',
   TASK_HISTORY_OLD_VALUE text  comment '',
   TASK_HISTORY_NEW_VALUE text not null  comment '',
   TASK_HISTORY_CHANGED_AT timestamp not null DEFAULT CURRENT_TIMESTAMP comment '',
   primary key (TASK_HISTORY_ID)
);

/*==============================================================*/
/* Table: TASK_STATUS                                           */
/*==============================================================*/
create table TASK_STATUS
(
   TASK_STATUS_ID       int not null auto_increment  comment '',
   SYSTEM_CODE          varchar(50) not null  comment '',
   SYSTEM_NAME          varchar(50) not null  comment '',
   SYSTEM_DESCRIPTION   text  comment '',
   primary key (TASK_STATUS_ID)
);

/*==============================================================*/
/* Table: USER                                                  */
/*==============================================================*/
create table USER
(
   USER_ID              int not null auto_increment  comment '',
   APP_ROLE_ID          int not null  comment '',
   USER_NAME            varchar(60) not null  comment '',
   USER_USERNAME        varchar(50) not null  comment '',
   USER_PASSWORD_HASHED varchar(255) not null  comment '',
   USER_EMAIL           varchar(150) not null  comment '',
   USER_IS_ACTIVE       bool not null default TRUE comment '',
   USER_ADDRESS         text  comment '',
   primary key (USER_ID),
   key AK_IDENTIFIER_2 (USER_USERNAME),
   key AK_IDENTIFIER_3 (USER_EMAIL)
);

/*==============================================================*/
/* Table: USER_NOTIFICATION                                     */
/*==============================================================*/
create table USER_NOTIFICATION
(
   NOTIFICATION_ID      int not null  comment '',
   USER_ID              int not null  comment '',
   USER_NOTIFICATION_IS_READ bool not null default FALSE comment '',
   USER_NOTIFICATION_READ_AT timestamp  comment '',
   primary key (NOTIFICATION_ID, USER_ID)
);

/*==============================================================*/
/* Table: USER_WIDGET_CONFIG                                    */
/*==============================================================*/
create table USER_WIDGET_CONFIG
(
   WIDGET_CONFIG_ID     int not null auto_increment  comment '',
   USER_ID              int not null  comment '',
   PLUGIN_ID            int not null  comment '',
   IS_VISIBLE           bool not null default TRUE comment '',
   WIDGET_CONFIG_POS_X  int  comment '',
   WIDGET_CONFIG_POS_Y  int  comment '',
   primary key (WIDGET_CONFIG_ID),
   key AK_IDENTIFIER_2 (USER_ID, PLUGIN_ID)
);

alter table APP_ROLE_PERMISSION add constraint FK_APP_ROLE_HAS_SYSTE_APP_ROLE foreign key (APP_ROLE_ID)
      references APP_ROLE (APP_ROLE_ID) on delete restrict on update restrict;

alter table APP_ROLE_PERMISSION add constraint FK_APP_ROLE_SYSTEM_RO_PERMISSI foreign key (PERMISSION_ID)
      references PERMISSION (PERMISSION_ID) on delete restrict on update restrict;

alter table PROJECT add constraint FK_PROJECT_CREATES_USER foreign key (PROJECT_CREATE_BY_ID)
      references USER (USER_ID) on delete restrict on update restrict;

alter table PROJECT add constraint FK_PROJECT_HAS_PROJE_PROJECT_ foreign key (PROJECT_STATUS_ID)
      references PROJECT_STATUS (PROJECT_STATUS_ID) on delete restrict on update restrict;

alter table PROJECT_MEMBER add constraint FK_PROJECT__BELONGS_T_PROJECT foreign key (PROJECT_ID)
      references PROJECT (PROJECT_ID) on delete restrict on update restrict;

alter table PROJECT_MEMBER add constraint FK_PROJECT__HAS_MEMBE_PROJECT_ foreign key (PROJECT_MEMBER_STATUS_ID)
      references PROJECT_MEMBER_STATUS (PROJECT_MEMBER_STATUS_ID) on delete restrict on update restrict;

alter table PROJECT_MEMBER add constraint FK_PROJECT__HAS_ROLE_PROJECT_ foreign key (PROJECT_ROLE_ID)
      references PROJECT_ROLE (PROJECT_ROLE_ID) on delete restrict on update restrict;

alter table PROJECT_MEMBER add constraint FK_PROJECT__INVITED_B_PROJECT_ foreign key (PROJECT_MEMBER_INVITE_ID)
      references PROJECT_MEMBER (PROJECT_MEMBER_ID) on delete restrict on update restrict;

alter table PROJECT_MEMBER add constraint FK_PROJECT__PARTICIPA_USER foreign key (USER_ID)
      references USER (USER_ID) on delete restrict on update restrict;

alter table PROJECT_ROLE_PERMISSION add constraint FK_PROJECT__HAS_PROJE_PROJECT_ foreign key (PROJECT_ROLE_ID)
      references PROJECT_ROLE (PROJECT_ROLE_ID) on delete restrict on update restrict;

alter table PROJECT_ROLE_PERMISSION add constraint FK_PROJECT__PROJECT_R_PERMISSI foreign key (PERMISSION_ID)
      references PERMISSION (PERMISSION_ID) on delete restrict on update restrict;

alter table TASK add constraint FK_TASK_HAS_TASKS_PROJECT foreign key (PROJECT_ID)
      references PROJECT (PROJECT_ID) on delete restrict on update restrict;

alter table TASK add constraint FK_TASK_HAS_TASK__TASK_STA foreign key (TASK_STATUS_ID)
      references TASK_STATUS (TASK_STATUS_ID) on delete restrict on update restrict;

alter table TASK add constraint FK_TASK_TASK_PRIO_PRIORITY foreign key (PRIORITY_ID)
      references PRIORITY (PRIORITY_ID) on delete restrict on update restrict;

alter table TASK_ASSIGNMENT add constraint FK_TASK_ASS_ASIGNMENT_TASK foreign key (TASK_ID)
      references TASK (TASK_ID) on delete cascade on update restrict;

alter table TASK_COMMENT add constraint FK_TASK_COM_CREATE_CO_PROJECT_ foreign key (PROJECT_MEMBER_ID)
      references PROJECT_MEMBER (PROJECT_MEMBER_ID) on delete restrict on update restrict;

alter table TASK_COMMENT add constraint FK_TASK_COM_HAS_COMME_TASK foreign key (TASK_ID)
      references TASK (TASK_ID) on delete cascade on update restrict;

alter table TASK_COMMENT add constraint FK_TASK_COM_TASK_COMM_TASK_COM foreign key (PARENT_ID)
      references TASK_COMMENT (TASK_COMMENT_ID) on delete cascade on update restrict;

alter table TASK_HISTORY add constraint FK_TASK_HIS_CHANGED_B_PROJECT_ foreign key (PROJECT_MEMBER_ID)
      references PROJECT_MEMBER (PROJECT_MEMBER_ID) on delete restrict on update restrict;

alter table TASK_HISTORY add constraint FK_TASK_HIS_HAS_HISTO_TASK foreign key (TASK_ID)
      references TASK (TASK_ID) on delete cascade on update restrict;

alter table USER add constraint FK_USER_HAS_SYSTE_APP_ROLE foreign key (APP_ROLE_ID)
      references APP_ROLE (APP_ROLE_ID) on delete restrict on update restrict;

alter table USER_NOTIFICATION add constraint FK_USER_NOT_NOTIFICAT_USER foreign key (USER_ID)
      references USER (USER_ID) on delete restrict on update restrict;

alter table USER_NOTIFICATION add constraint FK_USER_NOT_SEND_TO_U_NOTIFICA foreign key (NOTIFICATION_ID)
      references NOTIFICATION (NOTIFICATION_ID) on delete restrict on update restrict;

alter table USER_WIDGET_CONFIG add constraint FK_USER_WID_OWNS_USER foreign key (USER_ID)
      references USER (USER_ID) on delete restrict on update restrict;

alter table USER_WIDGET_CONFIG add constraint FK_USER_WID_PROVIDES__PLUGIN foreign key (PLUGIN_ID)
      references PLUGIN (PLUGIN_ID) on delete restrict on update restrict;


alter table TASK_ASSIGNMENT add constraint FK_TASK_ASSIGNMENT_ASSIGNER foreign key (TASK_ASSIGNER)
references PROJECT_MEMBER (PROJECT_MEMBER_ID) on delete restrict on update restrict;

alter table TASK_ASSIGNMENT add constraint FK_TASK_ASSIGNMENT_ASSIGNEE foreign key (TASK_ASSIGNEE)
      references PROJECT_MEMBER (PROJECT_MEMBER_ID) on delete restrict on update restrict;

/* Check cho bảng PROJECT */
ALTER TABLE PROJECT 
ADD CONSTRAINT CHK_PROJECT_DATES 
CHECK (PROJECT_END_AT >= PROJECT_START_AT);

ALTER TABLE PROJECT
ADD CONSTRAINT CHK_PROJECT_UPDATE
CHECK (PROJECT_UPDATE_AT >= PROJECT_CREATED_AT);

/* Check cho bảng TASK */
ALTER TABLE TASK 
ADD CONSTRAINT CHK_TASK_DEADLINE 
CHECK (TASK_DEADLINE IS NULL OR TASK_DEADLINE >= TASK_CREATED_AT);

/* Check cho bảng TASK_HISTORY, giá trị cũ và mới không được giống nhau */
ALTER TABLE TASK_HISTORY
ADD CONSTRAINT CHK_HISTORY_VALUE
CHECK (TASK_HISTORY_OLD_VALUE IS NULL 
       OR TASK_HISTORY_OLD_VALUE <> TASK_HISTORY_NEW_VALUE);

/* Check cho bảng PROJECT_MEMBER */
ALTER TABLE PROJECT_MEMBER 
ADD CONSTRAINT CHK_MEMBER_DATES 
CHECK (PROJECT_MEMBER_LEFT_AT IS NULL OR PROJECT_MEMBER_LEFT_AT >= PROJECT_MEMBER_JOIN_AT);

/* Check cho bảng USER_NOTIFICATION, nếu đã đọc thì phải có thời gian đọc */
ALTER TABLE USER_NOTIFICATION
ADD CONSTRAINT CHK_USER_NOTIFICATION_READ
CHECK (
    USER_NOTIFICATION_IS_READ = FALSE 
    OR USER_NOTIFICATION_READ_AT IS NOT NULL
);

ALTER TABLE APP_ROLE ADD CONSTRAINT UQ_APP_ROLE_CODE UNIQUE (SYSTEM_CODE);
ALTER TABLE PERMISSION ADD CONSTRAINT UQ_PERMISSION_CODE UNIQUE (SYSTEM_CODE);
ALTER TABLE PRIORITY ADD CONSTRAINT UQ_PRIORITY_CODE UNIQUE (SYSTEM_CODE);
ALTER TABLE PROJECT_STATUS ADD CONSTRAINT UQ_PROJECT_STATUS_CODE UNIQUE (SYSTEM_CODE);
ALTER TABLE TASK_STATUS ADD CONSTRAINT UQ_TASK_STATUS_CODE UNIQUE (SYSTEM_CODE);
ALTER TABLE PROJECT_MEMBER_STATUS ADD CONSTRAINT UQ_MEMBER_STATUS_CODE UNIQUE (SYSTEM_CODE);
ALTER TABLE PROJECT_ROLE ADD CONSTRAINT UQ_PROJECT_ROLE_CODE UNIQUE (SYSTEM_CODE);

ALTER TABLE USER ADD CONSTRAINT UQ_USER_USERNAME UNIQUE (USER_USERNAME);

ALTER TABLE USER ADD CONSTRAINT UQ_USER_EMAIL UNIQUE (USER_EMAIL);

ALTER TABLE PROJECT_MEMBER ADD CONSTRAINT UQ_PROJECT_MEMBER UNIQUE (USER_ID, PROJECT_ID);

ALTER TABLE TASK_ASSIGNMENT ADD CONSTRAINT UQ_TASK_ASSIGN UNIQUE (TASK_ID, TASK_ASSIGNEE);

ALTER TABLE USER_WIDGET_CONFIG ADD CONSTRAINT UQ_USER_PLUGIN UNIQUE (USER_ID, PLUGIN_ID);

ALTER TABLE PLUGIN ADD CONSTRAINT UQ_PLUGIN_NAME UNIQUE (PLUGIN_NAME);
ALTER TABLE PLUGIN ADD CONSTRAINT UQ_PLUGIN_MAIN_CLASS UNIQUE (PLUGIN_MAIN_CLASS);


-- ==============================================================================
-- Thêm dữ liệu mặc định
-- ==============================================================================

-- APP_ROLE (Vai trò hệ thống)
INSERT INTO APP_ROLE (SYSTEM_CODE, SYSTEM_NAME, SYSTEM_DESCRIPTION) VALUES 
('ADMIN', 'Quản trị viên', 'Toàn quyền quản lý hệ thống, người dùng và các cấu hình chung.'),
('USER', 'Người dùng', 'Người dùng thông thường, có thể tham gia vào các dự án.');

-- PROJECT_ROLE (Vai trò trong dự án)
INSERT INTO PROJECT_ROLE (SYSTEM_CODE, SYSTEM_NAME, SYSTEM_DESCRIPTION) VALUES 
('PM', 'Quản lý dự án', 'Người chịu trách nhiệm chính về dự án, điều phối công việc.'),
('CO_PM', 'Phó quản lý dự án', 'Người hỗ trợ quản lý dự án, phụ trách các công việc cụ thể trong dự án.'),
('MEMBER', 'Thành viên dự án', 'Người tham gia thực hiện các công việc trong dự án.'),
('VIEWER', 'Người xem', 'Chỉ có quyền xem thông tin dự án, không được chỉnh sửa.');

-- Region: PERMISSION (Quyền hạn hệ thống), được tách ra thành nhiều nhóm để dễ quản lý và phân quyền cho các Role khác nhau

-- ==============================================================================
-- QUẢN LÝ NGƯỜI DÙNG & HỆ THỐNG (User & System Management)
-- ==============================================================================
INSERT INTO PERMISSION (SYSTEM_CODE, SYSTEM_NAME, SYSTEM_DESCRIPTION) VALUES 
('USER_VIEW', 'Xem người dùng', 'Xem danh sách và thông tin chi tiết người dùng hệ thống.'),
('USER_CREATE', 'Tạo người dùng', 'Tạo tài khoản người dùng mới thủ công.'),
('USER_EDIT', 'Sửa người dùng', 'Cập nhật thông tin, trạng thái và đổi Role hệ thống cho người dùng.'),
('USER_DELETE', 'Xóa người dùng', 'Vô hiệu hóa tài khoản người dùng.'),
('LOOKUP_EDIT', 'Quản lý danh mục', 'Chỉnh sửa các bảng trạng thái, độ ưu tiên, phân loại (Lookup tables).'),
('PLUGIN_MANAGE', 'Quản lý Plugin', 'Cài đặt, gỡ bỏ và cấu hình các Plugin/Widget cho toàn hệ thống.');

-- ==============================================================================
-- QUẢN LÝ DỰ ÁN (Project Management)
-- ==============================================================================
INSERT INTO PERMISSION (SYSTEM_CODE, SYSTEM_NAME, SYSTEM_DESCRIPTION) VALUES 
('PROJECT_VIEW', 'Xem dự án', 'Xem danh sách các dự án được phép tham gia.'),
('PROJECT_CREATE', 'Tạo dự án', 'Khởi tạo một dự án mới (Người tạo mặc định là PM).'),
('PROJECT_EDIT', 'Sửa dự án', 'Thay đổi thông tin chung, ngày bắt đầu/kết thúc của dự án.'),
('PROJECT_DELETE', 'Xóa dự án', 'Xóa dự án hoặc chuyển vào trạng thái lưu trữ.'),
('PROJECT_STATS', 'Xem thống kê', 'Xem biểu đồ tiến độ và thống kê hiệu suất của dự án.');

-- ==============================================================================
-- QUẢN LÝ THÀNH VIÊN DỰ ÁN (Member Management)
-- ==============================================================================
INSERT INTO PERMISSION (SYSTEM_CODE, SYSTEM_NAME, SYSTEM_DESCRIPTION) VALUES 
('MEMBER_VIEW', 'Xem thành viên', 'Xem danh sách thành viên và vai trò trong dự án.'),
('MEMBER_INVITE', 'Mời thành viên', 'Gửi lời mời người dùng khác tham gia vào dự án.'),
('MEMBER_REMOVE', 'Xóa thành viên', 'Loại bỏ thành viên ra khỏi dự án.'),
('MEMBER_ROLE_EDIT', 'Sửa vai trò thành viên', 'Thay đổi quyền hạn/vai trò (PM, Dev, Test) của thành viên.');

-- ==============================================================================
-- QUẢN LÝ CÔNG VIỆC (Task Management)
-- ==============================================================================
INSERT INTO PERMISSION (SYSTEM_CODE, SYSTEM_NAME, SYSTEM_DESCRIPTION) VALUES 
('TASK_VIEW', 'Xem công việc', 'Xem danh sách và chi tiết các task trong dự án.'),
('TASK_CREATE', 'Tạo công việc', 'Tạo mới task và thiết lập deadline.'),
('TASK_EDIT', 'Sửa công việc', 'Chỉnh sửa nội dung mô tả và thời hạn của task.'),
('TASK_DELETE', 'Xóa công việc', 'Xóa task ra khỏi hệ thống.'),
('TASK_ASSIGN', 'Giao việc', 'Chỉ định người thực hiện hoặc người theo dõi task.'),
('TASK_STATUS_EDIT', 'Chuyển trạng thái task', 'Cập nhật tiến độ task (Ví dụ: Từ To-do sang Done).'),
('TASK_PRIORITY_EDIT', 'Sửa độ ưu tiên', 'Thay đổi mức độ khẩn cấp của công việc.'),
('TASK_HISTORY_VIEW', 'Xem lịch sử task', 'Xem nhật ký thay đổi của một công việc (Ai sửa, sửa gì, lúc nào).');

-- ==============================================================================
-- TƯƠNG TÁC & THÔNG BÁO (Collaboration & Notifications)
-- ==============================================================================
INSERT INTO PERMISSION (SYSTEM_CODE, SYSTEM_NAME, SYSTEM_DESCRIPTION) VALUES 
('COMMENT_CREATE', 'Gửi bình luận', 'Viết bình luận và đính kèm thông tin trong task.'),
('COMMENT_EDIT_OWN', 'Sửa bình luận cá nhân', 'Chỉnh sửa nội dung bình luận của chính mình.'),
('COMMENT_DELETE_OWN', 'Xóa bình luận cá nhân', 'Xóa bình luận của chính mình.'),
('COMMENT_DELETE_ANY', 'Xóa mọi bình luận', 'Quyền quản trị để xóa bình luận của người khác nếu vi phạm.'),
('NOTIFICATION_VIEW', 'Xem thông báo', 'Nhận và xem danh sách các thông báo cá nhân.');

-- ==============================================================================
-- CÁ NHÂN HÓA (Dashboard & Widgets)
-- ==============================================================================
INSERT INTO PERMISSION (SYSTEM_CODE, SYSTEM_NAME, SYSTEM_DESCRIPTION) VALUES 
('WIDGET_CONFIG', 'Cấu hình Dashboard', 'Tùy chỉnh vị trí, ẩn/hiện các widget trên màn hình cá nhân.');


-- PRIORITY (Mức độ ưu tiên)
INSERT INTO PRIORITY (SYSTEM_CODE, SYSTEM_NAME, SYSTEM_DESCRIPTION) VALUES 
('URGENT', 'Khẩn cấp', 'Cần giải quyết ngay lập tức, ảnh hưởng nghiêm trọng đến tiến độ.'),
('HIGH', 'Cao', 'Ưu tiên giải quyết sớm.'),
('MEDIUM', 'Trung bình', 'Giải quyết theo trình tự thông thường.'),
('LOW', 'Thấp', 'Có thể giải quyết sau khi rảnh.');

-- PROJECT_STATUS (Trạng thái dự án)
INSERT INTO PROJECT_STATUS (SYSTEM_CODE, SYSTEM_NAME, SYSTEM_DESCRIPTION) VALUES 
('PLANNING', 'Lập kế hoạch', 'Dự án đang trong giai đoạn chuẩn bị và khởi tạo.'),
('ACTIVE', 'Đang thực hiện', 'Dự án đang triển khai các hoạt động nghiệp vụ.'),
('ON_HOLD', 'Tạm dừng', 'Dự án tạm thời ngừng hoạt động vì lý do khách quan.'),
('COMPLETED', 'Đã hoàn thành', 'Dự án đã kết thúc thành công tất cả các mục tiêu.'),
('CANCELLED', 'Đã hủy', 'Dự án bị hủy bỏ và không tiếp tục triển khai.');

-- TASK_STATUS (Trạng thái công việc)
INSERT INTO TASK_STATUS (SYSTEM_CODE, SYSTEM_NAME, SYSTEM_DESCRIPTION) VALUES 
('TODO', 'Cần làm', 'Công việc vừa được tạo, chưa có người thực hiện.'),
('IN_PROGRESS', 'Đang làm', 'Công việc đang được xử lý bởi người phụ trách.'),
('REVIEW', 'Đang kiểm tra', 'Công việc đã xong, chờ quản lý hoặc khách hàng duyệt.'),
('DONE', 'Hoàn thành', 'Công việc đã được xác nhận hoàn thành.'),
('CANCELLED', 'Đã hủy', 'Công việc không còn cần thiết hoặc bị hủy bỏ.');

-- PROJECT_MEMBER_STATUS (Trạng thái thành viên trong dự án)
INSERT INTO PROJECT_MEMBER_STATUS (SYSTEM_CODE, SYSTEM_NAME, SYSTEM_DESCRIPTION) VALUES 
('PENDING', 'Đã mời', 'Người dùng đã được mời nhưng chưa xác nhận tham gia.'),
('ACTIVE', 'Đang tham gia', 'Thành viên đang hoạt động chính thức trong dự án.'),
('LEFT', 'Đã rời đi', 'Thành viên không còn thuộc dự án này.');


-- Gán quyền cho APP_ROLE: USER
INSERT INTO APP_ROLE_PERMISSION (APP_ROLE_ID, PERMISSION_ID)
SELECT ar.APP_ROLE_ID, p.PERMISSION_ID
FROM APP_ROLE ar, PERMISSION p
WHERE ar.SYSTEM_CODE = 'USER' 
AND p.SYSTEM_CODE IN (
    'PROJECT_VIEW',      
    'PROJECT_CREATE', 
    'NOTIFICATION_VIEW',
    'WIDGET_CONFIG'
);

-- Gán quyền cho APP_ROLE: ADMIN (Toàn quyền hệ thống)
INSERT INTO APP_ROLE_PERMISSION (APP_ROLE_ID, PERMISSION_ID)
SELECT ar.APP_ROLE_ID, p.PERMISSION_ID
FROM APP_ROLE ar, PERMISSION p
WHERE ar.SYSTEM_CODE = 'ADMIN' 
AND p.SYSTEM_CODE IN (
    'USER_VIEW', 'USER_CREATE', 'USER_EDIT', 'USER_DELETE', 
    'LOOKUP_EDIT',      
    'PLUGIN_MANAGE',    
    'PROJECT_STATS', 'PROJECT_STATS', 'PROJECT_VIEW', 'PROJECT_DELETE'
);

-- ==============================================================================
-- GÁN QUYỀN CHO PROJECT_ROLE (Nội bộ dự án)
-- ==============================================================================

-- 1. GÁN QUYỀN CHO VIEWER (Người xem - CHỈ XEM)
INSERT INTO PROJECT_ROLE_PERMISSION (PROJECT_ROLE_ID, PERMISSION_ID)
SELECT pr.PROJECT_ROLE_ID, p.PERMISSION_ID
FROM PROJECT_ROLE pr, PERMISSION p
WHERE pr.SYSTEM_CODE = 'VIEWER' 
AND p.SYSTEM_CODE IN (
      'PROJECT_VIEW',
      'MEMBER_VIEW',
      'TASK_VIEW'
);


-- 1. GÁN QUYỀN CHO MEMBER (Thành viên thông thường)

INSERT INTO PROJECT_ROLE_PERMISSION (PROJECT_ROLE_ID, PERMISSION_ID)
SELECT pr.PROJECT_ROLE_ID, p.PERMISSION_ID
FROM PROJECT_ROLE pr, PERMISSION p
WHERE pr.SYSTEM_CODE = 'MEMBER' 
AND p.SYSTEM_CODE IN (
    'PROJECT_VIEW',      
    'MEMBER_VIEW',      
    'TASK_VIEW',        
    'TASK_STATUS_EDIT',  
    'COMMENT_CREATE',    
    'COMMENT_EDIT_OWN'
);

-- ==============================================================================
-- 2. GÁN QUYỀN CHO CO_PM (Phó quản lý)
-- Gồm tất cả quyền của MEMBER + Quyền điều phối (Trừ DELETE dự án và ROLE_EDIT)
-- ==============================================================================
INSERT INTO PROJECT_ROLE_PERMISSION (PROJECT_ROLE_ID, PERMISSION_ID)
SELECT pr.PROJECT_ROLE_ID, p.PERMISSION_ID
FROM PROJECT_ROLE pr, PERMISSION p
WHERE pr.SYSTEM_CODE = 'CO_PM' 
AND p.SYSTEM_CODE IN (
    'PROJECT_VIEW', 'PROJECT_EDIT', 'PROJECT_STATS',
    'MEMBER_VIEW', 'MEMBER_INVITE', 'MEMBER_REMOVE',
    'TASK_VIEW', 'TASK_CREATE', 'TASK_EDIT', 'TASK_DELETE', 
    'TASK_ASSIGN', 'TASK_STATUS_EDIT', 'TASK_PRIORITY_EDIT', 'TASK_HISTORY_VIEW',
    'COMMENT_CREATE', 'COMMENT_EDIT_OWN', 'COMMENT_DELETE_ANY'
);

-- ==============================================================================
-- 3. GÁN QUYỀN CHO PM (Quản lý dự án - Toàn quyền nội bộ dự án)
-- ==============================================================================
INSERT INTO PROJECT_ROLE_PERMISSION (PROJECT_ROLE_ID, PERMISSION_ID)
SELECT pr.PROJECT_ROLE_ID, p.PERMISSION_ID
FROM PROJECT_ROLE pr, PERMISSION p
WHERE pr.SYSTEM_CODE = 'PM' 
AND (
    p.SYSTEM_CODE LIKE 'PROJECT_%' 
    OR p.SYSTEM_CODE IN (
    'MEMBER_VIEW', 'MEMBER_INVITE', 'MEMBER_REMOVE', 'MEMBER_ROLE_EDIT', 
    'TASK_VIEW', 'TASK_CREATE', 'TASK_EDIT', 'TASK_DELETE', 'TASK_ASSIGN', 
    'TASK_STATUS_EDIT', 'TASK_PRIORITY_EDIT', 'TASK_HISTORY_VIEW',
    'COMMENT_CREATE', 'COMMENT_EDIT_OWN', 'COMMENT_DELETE_ANY'
));

INSERT INTO USER (
    APP_ROLE_ID, USER_NAME, USER_USERNAME, USER_PASSWORD_HASHED, USER_EMAIL, USER_IS_ACTIVE, USER_ADDRESS
) 
VALUES (
    (SELECT APP_ROLE_ID FROM APP_ROLE WHERE SYSTEM_CODE = 'ADMIN'), 
    'System Administrator', 
    'admin', 
    '$2a$10$O958.bBMgjlq9MTtfzmx3OTT0mvitQSPSN3mrSUrwp4lRGFIB2xQm', -- admin
    'admin@system.com', 
    TRUE, 
    'Hệ thống'
);

INSERT INTO USER (
    APP_ROLE_ID, USER_NAME, USER_USERNAME, USER_PASSWORD_HASHED, USER_EMAIL, USER_IS_ACTIVE, USER_ADDRESS
) 
VALUES (
    (SELECT APP_ROLE_ID FROM APP_ROLE WHERE SYSTEM_CODE = 'USER'), 
    'Default User', 
    'user01', 
    '$2a$10$4IuCWDtW1UzrJFo.gBoG3ef1qihSpgL47q4n3CEDZ1tnZQU6TFjOW', -- 123456a
    'user01@system.com', 
    TRUE, 
    'Cần Thơ'
);



