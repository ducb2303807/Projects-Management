# Projects Management System
**Hệ thống quản lý dự án Nexus tối ưu dành cho nhóm làm việc hiện đại.**

<p align="left">
  <img src="https://img.shields.io/github/repo-size/ducb2303807/Projects-Management?style=for-the-badge" alt="repo-size">
  <img src="https://img.shields.io/github/license/ducb2303807/Projects-Management?style=for-the-badge&color=green" alt="license">
</p>

---

##  Giới thiệu dự án
**Nexus** là một ứng dụng hỗ trợ quản lý quy trình làm việc, giúp các thành viên trong nhóm theo dõi tiến độ, phân chia nhiệm vụ và quản lý thời gian hiệu quả. Dự án được xây dựng với mục tiêu tối giản hóa thao tác nhưng vẫn đảm bảo tính chuyên nghiệp trong quản trị.

## Thành viên

| Tên                    | MSSV     | Vai trò   | GitHub                                            |
| :--------------------- | :------- | :-------- | :------------------------------------------------ |
| **Dương Minh Đức**    | B2303807 | Leader    | [@ducb2303807](https://github.com/ducb2303807) - [@d3nhatv0lam](https://github.com/d3nhatv0lam) |
| **Võ Thành Nam**   | B2303829 | Member | [@NamIvsb02](https://github.com/NamIvsb02)    |
| **Đặng Nguyễn Gia Hân**  | B2303809 | Member    | [@lesyeuxdehunny](https://github.com/lesyeuxdehunny)    |
| **Võ Nguyễn Bảo Nghi**   | B2303830 | Member    | [@nghib2303830](https://github.com/nghib2303830/)           |
| **Nguyễn Nhất Phi** | B2303842 | Member    | [@zHe11Catz](https://github.com/zHe11Catz)    |

## Giảng viên

TS. Trương Minh Thái

##  Tính năng chính
* **Quản lý người dùng:** Đăng ký, đăng nhập và bảo mật thông tin tài khoản.
* **Quản lý dự án:** Tạo mới, chỉnh sửa, xóa và lưu trữ thông tin dự án.
* **Quản lý Task (Công việc):** Chia nhỏ công việc, gán người thực hiện và đặt Deadline.
* **Theo dõi trạng thái:** Cập nhật trạng thái công việc (To Do, In Progress, Done).
* **Tìm kiếm & Lọc:** Tìm kiếm nhanh các dự án theo tên hoặc thành viên phụ trách.

## Công nghệ sử dụng

| Thành phần | Công nghệ |
| :--- | :--- |
| **Frontend** | JavaFx (MVVMfx) |
| **Backend** | Spring Boot |
| **Database** | MySQL |
| **Authentication** | JSON Web Token (JWT) |
| **Version Control** | Git & GitHub |

## Cấu trúc mã nguồn
```text
Projects-Management/
├── common/
│   ├── dto/
│   ├── enums/
│   └── interfaces/
│
├── Projects_Management_BE/
│   ├── controller/
│   ├── service/
│   │   ├── base/
│   ├── repository/
│   │   └── base/
│   ├── entity/
│   ├── mapper/
│   ├── enums/
│   ├── core/
│   │   ├── config/
│   │   ├── security/
│   │   ├── exception/
│   │   ├── event/
│   │   ├── scheduler/
│   │   ├── strategy/
│   │   │   └── notification/
│   │   └── util/
│   └── exception-handler/
│
├── Projects_Management_FE/
│   ├── core/
│   │   ├── api/
│   │   │   ├── base/
│   │   │   ├── config/
│   │   │   └── utils/
│   │   ├── config/
│   │   ├── exception/
│   │   ├── navigation/
│   │   ├── plugin/
│   │   ├── session/
│   │   ├── extension/
│   │   ├── interfaces/
│   │   └── ui/
│   │
│   ├── features/
│   │   ├── auth/
│   │   ├── dashboard/
│   │   ├── mainlayout/
│   │   ├── project/
│   │   ├── task/
│   │   └── toast/
│   │
│   ├── ui/
│   │   ├── components/
│   │   └── styles/
│   │
│   └── resources/
│
├── plugins-source/
│   └── HelloWorldPlugin/
│
├── docs/
├── .github/workflows/
└── README.md
```

## Demo dự án

## Hướng dẫn cài đặt

### 1. Yêu cầu hệ thống (Prerequisites)
- **Java Development Kit (JDK):** Yêu cầu 21+. 
- **Database:** MySQL Server (phiên bản 8.0+).
- **Công cụ build:** Maven.
- **IDE:** IntelliJ IDEA (khuyên dùng), Eclipse hoặc VS Code.

### 2. Clone dự án

```bash
git clone https://github.com/ducb2303807/Projects-Management.git
cd Projects-Management
```

### 3. Cài đặt phụ thuộc

- Cài đặt theo lệnh sau và đợi đến khi hiện dòng `BUILD SUCCESS`

```bash
mvn clean install
```

### 4. Cài đặt database

- Tạo database mysql và import file `database.sql` [Tại đây](docs/projects_management.sql)

### 5. Cấu hình Backend

- Cấu hình lại `.env` file bằng file `.env.example`, chỉnh sửa cấu hình lại theo database của bạn

```
ADDRESS=<%= address %>
PORT=<%= port %>
DB_URL=<%= dbUrl %>
DB_USER=<%= dbUser %>
DB_PASS=<%= dbPass %>
```
- Cấu hình thông tin trong `src/main/resources/application.properties` nếu cần. Bạn cũng có thể thay đổi address và port server thông qua các file `*.properties`

```text
# Server name
spring.application.name=Projects_Management

# run for development
spring.profiles.active=dev
# run for production
#spring.profiles.active=prod
# run for test
#spring.profiles.active=test
```

### 6. Chạy Backend

- Chạy backend bằng lệnh sau

```bash
mvn spring-boot:run -pl Projects_Management_BE
```

### 7. Cấu hình Frontend

- Cấu hình lại `.env` file bằng file `.env.example`, chỉnh sửa cấu hình lại theo cấu hình của bạn

```
API_BASE_URL=<YOUR_API_SERVER_BASE_ENDPOINT_BASE_URL>
APP_TIMEOUT=<YOUR_APP_REQUEST_TIMEOUT>
```

### 8. Chạy Frontend

- Chạy frontend bằng lệnh sau

```bash
mvn org.openjfx:javafx-maven-plugin:0.0.8:run -pl Projects_Management_FE
```