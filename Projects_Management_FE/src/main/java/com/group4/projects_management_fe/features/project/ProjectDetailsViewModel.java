package com.group4.projects_management_fe.features.project;

import com.group4.common.dto.ProjectResponseDTO;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ProjectDetailsViewModel extends NewProjectViewModel {

    // Trạng thái Form (Mở lên mặc định là View Only - false)
    private final BehaviorSubject<Boolean> isEditing = BehaviorSubject.createDefault(false);
    private final BehaviorSubject<List<String>> members = BehaviorSubject.createDefault(new ArrayList<>());

    // Thêm subject cho Status
    private final BehaviorSubject<String> statusName = BehaviorSubject.createDefault("");

    // Thêm subject cho các label khác
    private final BehaviorSubject<String> createdBy = BehaviorSubject.createDefault("");
    private final BehaviorSubject<String> createdDate = BehaviorSubject.createDefault("");
    private final BehaviorSubject<String> lastUpdatedBy = BehaviorSubject.createDefault("");
    private final BehaviorSubject<String> lastUpdatedDate = BehaviorSubject.createDefault("");

    private Long currentProjectId;
    private ProjectResponseDTO originalData; // Lưu bản sao để sau này làm Phase 2 (Rollback)

    // ==========================================
    // PHASE 1: LOAD DỮ LIỆU TỪ API
    // ==========================================
    public void loadProjectDetails(Long projectId) {
        this.currentProjectId = projectId;

        projectApi.getProjectDetail(projectId).thenAccept(project -> {
            if (project == null) {
                System.err.println("❌ LỖI: API trả về null cho Project ID " + projectId);
                return;
            }
            this.originalData = project;

            // Nạp dữ liệu vào BehaviorSubject của cha
            if (project.getProjectName() != null) this.projectName.onNext(project.getProjectName());
            if (project.getDescription() != null) this.description.onNext(project.getDescription());

            // Parse an toàn từ LocalDateTime sang LocalDate
            if (project.getStartDate() != null) this.startDate.onNext(project.getStartDate().toLocalDate());
            if (project.getEndDate() != null) this.endDate.onNext(project.getEndDate().toLocalDate());

            // Nạp Status (Dựa trên ProjectBaseDTO)
            // Nếu bạn có getStatusName() thì dùng, không thì tạm để trống hoặc lấy theo StatusId
            if (project.getStatusName() != null) this.statusName.onNext(project.getStatusName());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy, hh:mm a");

            // Lấy tên người tạo (DTO của bạn có userCreatedFullName hoặc userCreatedUsername)
            if (project.getUserCreatedFullName() != null) {
                this.createdBy.onNext(project.getUserCreatedFullName());
            } else if (project.getUserCreatedUsername() != null) {
                this.createdBy.onNext(project.getUserCreatedUsername());
            }

             if (project.getCreatedAt() != null) {
                 this.createdDate.onNext(project.getCreatedAt().format(formatter));
             }

//             if (project.get() != null) {
//                 this.lastUpdatedBy.onNext(project.getUserUpdatedFullName());
//             }

             if (project.getUpdateAt() != null) {
                 this.lastUpdatedDate.onNext(project.getUpdateAt().format(formatter));
             }

        }).exceptionally(ex -> {
            System.err.println("Lỗi gọi API getProjectDetail: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        });
    }

    // ==========================================
    // CÁC HÀM OBSERVABLE CHO UI LẮNG NGHE
    // ==========================================
    public Observable<Boolean> isEditingObservable() { return isEditing.hide(); }
    public Observable<String> projectNameObservable() { return this.projectName.hide(); }
    public Observable<String> descriptionObservable() { return this.description.hide(); }
    public Observable<LocalDate> startDateObservable() { return this.startDate.hide(); }
    public Observable<LocalDate> endDateObservable() { return this.endDate.hide(); }
    public Observable<String> statusNameObservable() { return this.statusName.hide(); }
    public Observable<String> createdByObservable() { return createdBy.hide(); }
    public Observable<String> createdDateObservable() { return createdDate.hide(); }
    public Observable<String> lastUpdatedByObservable() { return lastUpdatedBy.hide(); }
    public Observable<String> lastUpdatedDateObservable() { return lastUpdatedDate.hide(); }

    // ==========================================
    // CÁC HÀM STATE & MOCK CHO NÚT BẤM (ĐỂ FXML KHÔNG BỊ LỖI)
    // ==========================================
    public void enableEditMode() { isEditing.onNext(true); }
    public void cancelEditMode() { isEditing.onNext(false); }
    public void saveChanges() { isEditing.onNext(false); } // Chờ Phase 2 làm tiếp PUT API

    public void addMember(String username) {}
    public void removeMember(String username) {}
    public Observable<List<String>> membersObservable() { return members.hide(); }
}