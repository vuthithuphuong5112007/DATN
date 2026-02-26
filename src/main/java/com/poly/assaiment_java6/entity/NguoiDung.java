package com.poly.assaiment_java6.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
@Entity
@Table(name = "NguoiDung") // Tên bảng trong SQL Server
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NguoiDung {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // IDENTITY cho SQL Server
    @Column(name = "ID_NguoiDung")
    private Integer idNguoiDung;

    @NotBlank(message = "Họ tên không được để trống")
    @Column(name = "HoTen")
    private String hoTen;

    // 2. Tên đăng nhập
    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Column(name = "TenDangNhap", nullable = false, unique = true)
    private String tenDangNhap;

    // 3. Mật khẩu (Không mã hóa - Plain text)
    @NotBlank(message = "Mật khẩu không được để trống")
    @Column(name = "MatKhau", nullable = false)
    private String matKhau;

    // 4. Vai trò (ROLE_USER, ROLE_ADMIN)
    @Column(name = "VaiTro")
    private String vaiTro = "USER"; // Mặc định là "USER" trong Entity

    // THAY BẰNG ANNOTATION NÀY (sử dụng Regex kiểm tra nghiêm ngặt hơn):
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",
            message = "Email không đúng định dạng. Ví dụ: user@example.com")
    @NotBlank(message = "Email không được để trống") // Giữ lại NotBlank
    @Column(name = "Email", unique = true)
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^\\d{10}$", message = "Số điện thoại phải có đúng 10 chữ số")
    @Column(name = "SoDienThoai") // Đã sửa tên cột
    private String sdt;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Column(name = "DiaChi")
    private String diaChi;
}
