# Badminton Court Booking System – Java Web Service

## Tech Stack
- Spring Boot 3.x, Spring Security, JWT, JPA, MySQL, Redis, Cloudinary

## Cách chạy
1. Tạo database MySQL tên: `badminton_db`
2. Cập nhật `application.properties` (datasource, jwt.secret, cloudinary keys, redis)
3. Khởi động server Redis (mặc định chạy tại cổng `6379`)
4. Khởi chạy ứng dụng bằng Gradle:
   ```powershell
   ./gradlew bootRun
   ```

## API Base URL
`http://localhost:8080/api/v1`

## Tài khoản test mặc định (seed data)
*   **Admin**: `admin@test.com` / `Admin@123`
*   **Manager**: `manager@test.com` / `Manager@123`
*   **Customer**: `customer@test.com` / `Customer@123`

## Danh sách API chính (FR-01 → FR-13)
*   **FR-01 (Đăng nhập)**: `POST /api/v1/auth/login`
*   **FR-02 (Làm mới Token)**: `POST /api/v1/auth/refresh`
*   **FR-03 (Đăng xuất / Vô hiệu hóa JWT)**: `POST /api/v1/auth/logout`
*   **FR-04 (Đăng ký tài khoản)**: `POST /api/v1/auth/register`
*   **FR-05 (Quản lý User - Admin)**:
    *   Xem danh sách: `GET /api/v1/admin/users?page=0&size=10&search=keyword`
    *   Xem chi tiết: `GET /api/v1/admin/users/{id}`
    *   Cập nhật thông tin: `PUT /api/v1/admin/users/{id}`
    *   Xóa tài khoản: `DELETE /api/v1/admin/users/{id}`
*   **FR-06 (Đặt lịch sân cầu lông)**: `POST /api/v1/customer/bookings`
*   **FR-07 (Xem lịch sử đặt sân)**: `GET /api/v1/customer/bookings`
*   **FR-08 (Duyệt/Từ chối đặt sân - Manager)**:
    *   Phê duyệt: `PUT /api/v1/manager/bookings/{id}/approve`
    *   Từ chối: `PUT /api/v1/manager/bookings/{id}/reject`
*   **FR-09 (Tải ảnh sân lên Cloudinary)**: `POST /api/v1/manager/courts/{courtId}/images`
*   **FR-10 (Đổi mật khẩu)**: `PUT /api/v1/auth/change-password`
*   **FR-11 (Quên mật khẩu)**: `POST /api/v1/auth/forgot-password`
*   **FR-12 (AOP Logging)**: Tự động ghi nhật ký kiểm toán (Audit) khi đặt sân thành công/thất bại và đo hiệu năng thời gian xử lý của các phương thức service.
*   **FR-13 (Redis Token Blacklist)**: Sử dụng Redis RAM (TTL tương đương hạn dùng của JWT) để lưu các token đã đăng xuất nhằm vô hiệu hóa truy cập tiếp theo một cách tối ưu nhất.
