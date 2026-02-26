package com.poly.assaiment_java6.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO chứa thông tin tóm tắt người dùng, dùng cho việc hiển thị trong bảng hoặc danh sách.
 * Sử dụng thư viện Lombok để tự động tạo getter, setter, constructors.
 */
@Data
public class NguoiDungDto {
    private Integer idNguoiDung;
    private String hoTen;
    private String tenDangNhap; // Thường là email hoặc username
    private String email;
    private String sdt;
    private String diaChi;
    private String vaiTro; // USER, ADMIN, OWNER
}
