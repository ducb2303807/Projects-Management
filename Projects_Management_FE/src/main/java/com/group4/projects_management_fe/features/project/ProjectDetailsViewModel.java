package com.group4.projects_management_fe.features.project;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProjectDetailsViewModel extends NewProjectViewModel {

    // --- STATE MỚI: Đang ở Phase 1 (false) hay Phase 2 (true)? ---
    private final BehaviorSubject<Boolean> isEditing = BehaviorSubject.createDefault(false);
    private final BehaviorSubject<List<String>> members = BehaviorSubject.createDefault(new ArrayList<>());

    // Hàm kích hoạt chế độ chỉnh sửa
    public void enableEditMode() {
        isEditing.onNext(true);
    }

    // Hàm hủy bỏ chỉnh sửa quay về chế độ xem
    public void cancelEditMode() {
        isEditing.onNext(false);
        // TODO: Cần reload lại dữ liệu gốc từ DB/API để xóa các thay đổi chưa lưu
    }

    public Observable<Boolean> isEditingObservable() {
        return isEditing.hide();
    }

    public void saveChanges() {
        System.out.println("--- GỌI API UPDATE PROJECT ---");
        // Gọi API lưu dữ liệu, sau khi lưu thành công thì quay về View Mode
        isEditing.onNext(false);
    }
    public void addMember(String username) {
        if (username == null || username.trim().isEmpty()) return;
        List<String> current = new ArrayList<>(members.getValue());
        if (!current.contains(username.trim())) {
            current.add(username.trim());
            members.onNext(current);
        }
    }

    public void removeMember(String username) {
        List<String> current = new ArrayList<>(members.getValue());
        current.remove(username);
        members.onNext(current);
    }

    public Observable<List<String>> membersObservable() {
        return members.hide();
    }
}