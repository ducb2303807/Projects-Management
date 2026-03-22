package com.group4.projects_management_fe.features.project;

import com.group4.common.dto.*;
import com.group4.projects_management_fe.core.api.UserApi;
import com.group4.projects_management_fe.core.session.AppSessionManager;
import com.group4.projects_management_fe.core.api.LookupApi;
import com.group4.common.enums.LookupType;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import javafx.application.Platform;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProjectDetailsViewModel extends NewProjectViewModel {

    private final LookupApi lookupApi = new LookupApi(AppSessionManager.getInstance());

    // Trạng thái Form (Mở lên mặc định là View Only - false)
    private final BehaviorSubject<Boolean> isEditing = BehaviorSubject.createDefault(false);
    private final BehaviorSubject<List<String>> members = BehaviorSubject.createDefault(new ArrayList<>());

    // Thêm subject cho Status (Lưu Tên hiển thị)
    private final BehaviorSubject<String> statusName = BehaviorSubject.createDefault("");

    // Thêm subject cho các label Audit
    private final BehaviorSubject<String> createdBy = BehaviorSubject.createDefault("");
    private final BehaviorSubject<String> createdDate = BehaviorSubject.createDefault("");
    private final BehaviorSubject<String> lastUpdatedBy = BehaviorSubject.createDefault("");
    private final BehaviorSubject<String> lastUpdatedDate = BehaviorSubject.createDefault("");

    // State lưu danh sách tên Status để hiển thị lên UI (ComboBox)
    private final BehaviorSubject<List<String>> statusList = BehaviorSubject.createDefault(new ArrayList<>());

    // Subject quản lý danh sách thành viên
    private final BehaviorSubject<List<ProjectMemberDTO>> projectMembers = BehaviorSubject.createDefault(new ArrayList<>());

    // Subject quản lý kết quả search
    private final BehaviorSubject<List<UserDTO>> coManagerSearchResults = BehaviorSubject.createDefault(new ArrayList<>());
    private final BehaviorSubject<List<UserDTO>> memberSearchResults = BehaviorSubject.createDefault(new ArrayList<>());

    // Map dùng để quy đổi ngược từ: Tên Status (String) -> Status ID (Long) khi Save
    private final Map<String, Long> statusNameToIdMap = new HashMap<>();
    public Map<String, Long> getStatusNameToIdMap() {
        return statusNameToIdMap;
    }

    private final io.reactivex.rxjava3.subjects.PublishSubject<Boolean> saveSuccess = io.reactivex.rxjava3.subjects.PublishSubject.create();
    private final io.reactivex.rxjava3.subjects.PublishSubject<String> saveError = io.reactivex.rxjava3.subjects.PublishSubject.create();
    private final io.reactivex.rxjava3.subjects.PublishSubject<Boolean> deleteSuccess = io.reactivex.rxjava3.subjects.PublishSubject.create();


    private Long currentProjectId;
    private ProjectResponseDTO originalData; // Lưu bản sao để Rollback

    // ==========================================
    // CÁC HÀM OBSERVABLE CHO UI LẮNG NGHE
    // ==========================================
    public Observable<Boolean> isEditingObservable() { return isEditing.hide(); }
    public Observable<String> projectNameObservable() { return this.projectName.hide(); }
    public Observable<String> descriptionObservable() { return this.description.hide(); }
    public Observable<LocalDate> startDateObservable() { return this.startDate.hide(); }
    public Observable<LocalDate> endDateObservable() { return this.endDate.hide(); }
    public Observable<Boolean> onDeleteSuccess() { return deleteSuccess.hide(); }
    public Observable<String> statusNameObservable() { return this.statusName.hide(); }
    public Observable<List<ProjectMemberDTO>> getProjectMembersObservable() { return projectMembers; }
    public Observable<List<UserDTO>> getCoManagerSearchResults() { return coManagerSearchResults; }
    public Observable<List<UserDTO>> getMemberSearchResults() { return memberSearchResults; }

    // HÀM MỚI ĐƯỢC THÊM VÀO ĐỂ NHẬN GIÁ TRỊ TỪ COMBOBOX:
    public void setStatusName(String name) { this.statusName.onNext(name); }

    public Observable<List<String>> statusListObservable() { return statusList.hide(); }
    public Observable<String> createdByObservable() { return createdBy.hide(); }
    public Observable<String> createdDateObservable() { return createdDate.hide(); }
    public Observable<String> lastUpdatedByObservable() { return lastUpdatedBy.hide(); }
    public Observable<String> lastUpdatedDateObservable() { return lastUpdatedDate.hide(); }

    public Observable<Boolean> onSaveSuccess() { return saveSuccess.hide(); }
    public Observable<String> onSaveError() { return saveError.hide(); }
    public Observable<List<String>> membersObservable() { return members.hide(); }

    public void enableEditMode() { isEditing.onNext(true); }
    public void addMember(String username) {}
    public void removeMember(String username) {}
    private final UserApi userApi = new UserApi(AppSessionManager.getInstance());

    public List<UserDTO> getCurrentCoManagerList() {
        return coManagerSearchResults.getValue();
    }

    public List<UserDTO> getCurrentMemberList() {
        return memberSearchResults.getValue();
    }

    // ==========================================
    // PHASE 1: LOAD DỮ LIỆU TỪ API (TÍCH HỢP LOOKUP)
    // ==========================================
    public void loadProjectDetails(Long projectId) {
        this.currentProjectId = projectId;

        // 1. Gọi API lấy danh sách Status động trước
        lookupApi.getAll(LookupType.PROJECT_STATUS)
                .thenCompose(lookups -> {
                    List<String> names = new ArrayList<>();
                    statusNameToIdMap.clear();

                    // Đổ dữ liệu vào Map và List
                    for (LookupDTO dto : lookups) {
                        //Bỏ qua trạng thái "Đã hủy" (ID = 5)
                        if ("5".equals(String.valueOf(dto.getId()))) {
                            continue;
                        }

                        String name = dto.getName();
                        names.add(name);
                        statusNameToIdMap.put(name, Long.parseLong(String.valueOf(dto.getId()))); // Đảm bảo ép kiểu an toàn
                    }
                    statusList.onNext(names); // Bắn danh sách ra UI

                    // 2. Load xong Status thì gọi tiếp API lấy chi tiết Project
                    return projectApi.getProjectDetail(projectId);
                })
                .thenAccept(project -> {
                    if (project == null) {
                        System.err.println("❌ LỖI: API trả về null cho Project ID " + projectId);
                        return;
                    }
                    this.originalData = project;

                    // Nạp dữ liệu vào BehaviorSubject
                    if (project.getProjectName() != null) this.projectName.onNext(project.getProjectName());
                    if (project.getDescription() != null) this.description.onNext(project.getDescription());

                    if (project.getStartDate() != null) this.startDate.onNext(project.getStartDate().toLocalDate());
                    if (project.getEndDate() != null) this.endDate.onNext(project.getEndDate().toLocalDate());

                    // Nạp Status từ Database lên UI
                    if (project.getStatusName() != null) {
                        this.statusName.onNext(project.getStatusName());
                    }

                    // Format ngày tháng cho Audit
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy, hh:mm a");

                    if (project.getUserCreatedFullName() != null) {
                        this.createdBy.onNext(project.getUserCreatedFullName());
                    } else if (project.getUserCreatedUsername() != null) {
                        this.createdBy.onNext(project.getUserCreatedUsername());
                    }

                    if (project.getCreatedAt() != null) {
                        this.createdDate.onNext(project.getCreatedAt().format(formatter));
                    }

                    if (project.getUpdateAt() != null) {
                        this.lastUpdatedDate.onNext(project.getUpdateAt().format(formatter));
                    }
                })
                .exceptionally(ex -> {
                    System.err.println("Lỗi gọi API load Project/Lookup: " + ex.getMessage());
                    ex.printStackTrace();
                    return null;
                });
        fetchProjectMembers();
    }

    // ==========================================
    // PHASE 2: UPDATE & ROLLBACK
    // ==========================================

    // Hủy sửa: Rollback lại dữ liệu từ bản gốc
    public void cancelEditMode() {
        if (originalData != null) {
            if (originalData.getProjectName() != null) this.projectName.onNext(originalData.getProjectName());
            if (originalData.getDescription() != null) this.description.onNext(originalData.getDescription());

            if (originalData.getStartDate() != null) this.startDate.onNext(originalData.getStartDate().toLocalDate());
            if (originalData.getEndDate() != null) this.endDate.onNext(originalData.getEndDate().toLocalDate());

            if (originalData.getStatusName() != null) this.statusName.onNext(originalData.getStatusName());
        }
        isEditing.onNext(false); // Khóa form
    }

    // Lưu thay đổi: Gọi API PUT
    public void saveChanges() {
        if (currentProjectId == null) return;

        ProjectUpdateRequestDTO dto = new ProjectUpdateRequestDTO();
        dto.setProjectName(projectName.getValue());
        dto.setDescription(description.getValue());

        if (startDate.getValue() != null && !startDate.getValue().equals(LocalDate.MIN)) {
            dto.setStartDate(startDate.getValue().atStartOfDay());
        }
        if (endDate.getValue() != null && !endDate.getValue().equals(LocalDate.MIN)) {
            dto.setEndDate(endDate.getValue().atTime(23, 59, 59));
        }

        // TÍCH HỢP QUY ĐỔI STATUS ID ĐỂ GỬI XUỐNG API:
        String currentStatusName = statusName.getValue();
        if (currentStatusName != null && statusNameToIdMap.containsKey(currentStatusName)) {
            dto.setStatusId(statusNameToIdMap.get(currentStatusName));
        }

        // Gọi API Cập nhật
        projectApi.updateProject(currentProjectId, dto).thenAccept(updatedProject -> {
            this.originalData = updatedProject; // Cập nhật bản ghi gốc
            saveSuccess.onNext(true);
            isEditing.onNext(false);
        }).exceptionally(ex -> {
            System.err.println("Lỗi cập nhật dự án: " + ex.getMessage());
            saveError.onNext(ex.getMessage());
            return null;
        });
    }

    // XÓA HÀM softDeleteProject() cũ đi và thay bằng hàm này:
    public void deleteProject() {
        if (currentProjectId == null) return;

        // Gọi thẳng endpoint DELETE của backend
        projectApi.deleteProject(currentProjectId).thenAccept(v -> {
            //Xóa dự án có ID tương ứng ra khỏi local
            com.group4.projects_management_fe.features.project.ProjectController.recentProjectsList
                    .removeIf(p -> String.valueOf(p.getId()).equals(String.valueOf(currentProjectId)));
            // Báo thành công để Controller đóng form
            deleteSuccess.onNext(true);
        }).exceptionally(ex -> {
            System.err.println("Lỗi khi xóa dự án: " + ex.getMessage());
            saveError.onNext("Lỗi xóa dự án: " + ex.getMessage());
            return null;
        });
    }

    public void fetchProjectMembers() {
        if (currentProjectId == null) return;

        projectApi.getMembersOfProject(currentProjectId)
                .thenAccept(membersList -> {
                    // Đẩy dữ liệu mới vào Subject
                    projectMembers.onNext(membersList);
                })
                .exceptionally(ex -> {
                    System.err.println("Lỗi khi lấy danh sách thành viên: " + ex.getMessage());
                    return null;
                });
    }

    // ==========================================
    // LOGIC TÌM KIẾM CO-MANAGER
    // ==========================================
    public void searchUsersForCoManager(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            coManagerSearchResults.onNext(new ArrayList<>());
            return;
        }
        // Gọi API: GET /api/users/search
        userApi.searchUsers(keyword).thenAccept(users -> {
            // Lọc bỏ những người ĐÃ LÀ Co-Manager hiện tại
            List<UserDTO> filtered = users.stream()
                    .filter(u -> !isAlreadyCoManager(u.getId()))
                    .collect(Collectors.toList());
            coManagerSearchResults.onNext(filtered);
        }).exceptionally(ex -> {
            ex.printStackTrace(); return null;
        });
    }

    private boolean isAlreadyCoManager(Long userId) {
        return projectMembers.getValue().stream()
                .anyMatch(m -> m.getUserId().equals(userId)
                        && ("Co-Project Manager".equalsIgnoreCase(m.getRoleName()) || "Project Manager".equalsIgnoreCase(m.getRoleName()))
                        && "Active".equalsIgnoreCase(m.getStatusName()));
    }

    // ==========================================
    // LOGIC TÌM KIẾM MEMBER
    // ==========================================
    public void searchUsersForMember(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            memberSearchResults.onNext(new ArrayList<>());
            return;
        }
        userApi.searchUsers(keyword).thenAccept(users -> {
            // Lọc bỏ những người ĐÃ LÀ MEMBER hoặc CO-MANAGER (Tức là đang Active trong project)
            List<UserDTO> filtered = users.stream()
                    .filter(u -> !isAlreadyInProject(u.getId()))
                    .collect(Collectors.toList());
            memberSearchResults.onNext(filtered);
        }).exceptionally(ex -> {
            ex.printStackTrace(); return null;
        });
    }

    private boolean isAlreadyInProject(Long userId) {
        return projectMembers.getValue().stream()
                .anyMatch(m -> m.getUserId().equals(userId) && "Active".equalsIgnoreCase(m.getStatusName()));
    }

    // ==========================================
    // LOGIC MỜI THAM GIA
    // ==========================================
    public void inviteUser(Long userId, Long roleId) {
        if (currentProjectId == null) return;

        // 1. Sử dụng Builder của MemberInviteRequest (Chỉ cần userId và roleId)
        MemberInviteRequest inviteDTO = MemberInviteRequest.builder()
                .userId(userId)             // Gắn User ID được mời
                .roleId(roleId)             // Gắn Role ID (2 cho Co-manager, 3 cho Member)
                .build();

        // 2. Bọc vào List vì API endpoint yêu cầu List<MemberInviteRequest>
        List<MemberInviteRequest> requestBody = List.of(inviteDTO);

        // 3. Gọi API: projectId truyền vào tham số đầu, list truyền vào tham số thứ hai
        projectApi.inviteMembers(currentProjectId, requestBody).thenAccept(v -> {
            // Mời thành công -> Tự động gọi hàm fetch lại danh sách thành viên để cập nhật UI
            javafx.application.Platform.runLater(this::fetchProjectMembers);
        }).exceptionally(ex -> {
            System.err.println("Lỗi khi mời thành viên: " + ex.getMessage());
            return null;
        });
    }

    // ==========================================
    // LOGIC RỜI KHỎI DỰ ÁN (LEAVE)
    // ==========================================
    public void leaveProject(Long projectMemberId) {
        if (projectMemberId == null) return;

        projectApi.removeMemberFromProject(projectMemberId).thenAccept(v -> {
            // Khi leave thành công, ta mượn luôn deleteSuccess để báo cho Controller đóng Popup
            // và tự động refresh lại danh sách Project bên ngoài (y hệt như khi xóa dự án)
            Platform.runLater(() -> deleteSuccess.onNext(true));
        }).exceptionally(ex -> {
            System.err.println("Lỗi khi rời dự án: " + ex.getMessage());
            return null;
        });
    }

    // ==========================================
    // LOGIC KICK MEMBER (XÓA KHỎI DỰ ÁN)
    // ==========================================
    public void kickMember(Long projectMemberId) {
        if (projectMemberId == null) return;

        projectApi.removeMemberFromProject(projectMemberId).thenAccept(v -> {
            System.out.println("Kick member thành công!");
            // Cập nhật lại UI sau khi kick thành công
            Platform.runLater(() -> {
                fetchProjectMembers(); // Gọi lại hàm load members, danh sách sẽ tự động render lại
            });
        }).exceptionally(ex -> {
            System.err.println("Lỗi khi kick member: " + ex.getMessage());
            // Bạn có thể emit lỗi ra UI nếu cần
            return null;
        });
    }
    public Observable<Boolean> getIsEditing() {
        return isEditing;
    }
}