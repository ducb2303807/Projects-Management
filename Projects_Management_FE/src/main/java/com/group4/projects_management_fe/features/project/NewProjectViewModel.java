package com.group4.projects_management_fe.features.project;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NewProjectViewModel {

    // --- 1. STATE (Trạng thái của Form) ---
    private final BehaviorSubject<String> projectName = BehaviorSubject.createDefault("");
    private final BehaviorSubject<LocalDate> startDate = BehaviorSubject.createDefault(LocalDate.MIN);
    private final BehaviorSubject<LocalDate> endDate = BehaviorSubject.createDefault(LocalDate.MIN);
    private final BehaviorSubject<String> status = BehaviorSubject.createDefault("");
    private final BehaviorSubject<String> description = BehaviorSubject.createDefault("");
    private final BehaviorSubject<List<String>> coManagers = BehaviorSubject.createDefault(new ArrayList<>());

    // --- 2. INPUT (View đẩy dữ liệu xuống) ---
    public void setProjectName(String name) { projectName.onNext(name); }
    public void setStartDate(LocalDate date) { startDate.onNext(date != null ? date : LocalDate.MIN); }
    public void setEndDate(LocalDate date) { endDate.onNext(date != null ? date : LocalDate.MIN); }
    public void setStatus(String stat) { status.onNext(stat); }
    public void setDescription(String desc) { description.onNext(desc); }

    // Logic thêm Co-Manager
    public void addCoManager(String username) {
        if (username == null || username.trim().isEmpty()) return;
        List<String> current = new ArrayList<>(coManagers.getValue());
        if (!current.contains(username.trim())) {
            current.add(username.trim());
            coManagers.onNext(current); // Phát tín hiệu đã có danh sách mới
        }
    }

    // Logic xóa Co-Manager
    public void removeCoManager(String username) {
        List<String> current = new ArrayList<>(coManagers.getValue());
        current.remove(username);
        coManagers.onNext(current);
    }

    // --- 3. OUTPUT (Cung cấp Observable để View lắng nghe) ---
    public Observable<List<String>> coManagersObservable() {
        return coManagers.hide();
    }

    // Logic kết hợp: Kiểm tra Form hợp lệ (Tên không rỗng và có chọn Start Date)
    // Sẽ dùng để Bật/Tắt nút Create trên giao diện
    public Observable<Boolean> isFormValidObservable() {
        return Observable.combineLatest(
                projectName,
                startDate,
                (name, start) -> !name.trim().isEmpty() && !start.equals(LocalDate.MIN)
        );
    }

    // Hàm gom dữ liệu chuẩn bị gọi API (Sẽ dùng ở bước sau)
    public void submitProject() {
        System.out.println("--- CALL API TẠO PROJECT ---");
        System.out.println("Name: " + projectName.getValue());
        System.out.println("Start Date: " + startDate.getValue());
        System.out.println("Co-Managers: " + coManagers.getValue());
        // TODO: Gọi Service -> Retrofit API ở đây
    }
}