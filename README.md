# 🚀 Projects Management System
**Hệ thống quản lý dự án tối ưu dành cho nhóm làm việc hiện đại.**

<p align="left">
  <img src="https://img.shields.io/github/repo-size/ducb2303807/Projects-Management?style=for-the-badge" alt="repo-size">
  <img src="https://img.shields.io/github/license/ducb2303807/Projects-Management?style=for-the-badge&color=green" alt="license">
</p>

---

## 📌 Giới thiệu dự án
**Projects Management** là một ứng dụng hỗ trợ quản lý quy trình làm việc, giúp các thành viên trong nhóm theo dõi tiến độ, phân chia nhiệm vụ và quản lý thời gian hiệu quả. Dự án được xây dựng với mục tiêu tối giản hóa thao tác nhưng vẫn đảm bảo tính chuyên nghiệp trong quản trị.

## ✨ Tính năng chính
* **👤 Quản lý người dùng:** Đăng ký, đăng nhập và bảo mật thông tin tài khoản.
* **📂 Quản lý dự án:** Tạo mới, chỉnh sửa, xóa và lưu trữ thông tin dự án.
* **📝 Quản lý Task (Công việc):** Chia nhỏ công việc, gán người thực hiện và đặt Deadline.
* **📊 Theo dõi trạng thái:** Cập nhật trạng thái công việc (To Do, In Progress, Done).
* **🔍 Tìm kiếm & Lọc:** Tìm kiếm nhanh các dự án theo tên hoặc thành viên phụ trách.

## 🛠 Công nghệ sử dụng

| Thành phần | Công nghệ |
| :--- | :--- |
| **Frontend** | JavaFx (MVVMfx) |
| **Backend** | Spring Boot |
| **Database** | MySQL |
| **Authentication** | JSON Web Token (JWT) |
| **Version Control** | Git & GitHub |

## 📂 Cấu trúc mã nguồn
```text
Projects-Management/
├── common/
│   ├── dto/
│   ├── interfaces/
│
├── Projects_Management_BE/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   ├── core/
│   │   ├── config/
│   │   ├── security/
│   │   ├── exception/
│   │   └── util/
│   └── exception-handler/
│
├── Projects_Management_FE/
│   ├── core/
│   │   ├── api/
│   │   ├── di/
│   │   ├── navigation/
│   │   └── events/
│   ├── features/
│   │   └── project_list/
│   ├── ui/
│   │   ├── components/
│   │   └── styles/
│   └── resources/
│
├── docs/
└── README.md
```

## 📸 Demo dự án

## ⚙️ Hướng dẫn cài đặt

### 1. Yêu cầu hệ thống (Prerequisites)
- **Java Development Kit (JDK):** Yêu cầu 21+. 
- **Database:** MySQL Server (phiên bản 8.0+).
- **Công cụ build:** Maven.
- **IDE:** IntelliJ IDEA (khuyên dùng), Eclipse hoặc VS Code.

1. Clone dự án
2. Cấu hình Backend
3. Cấu hình Frontend
