package com.poly.assaiment_java6.service;

import com.poly.assaiment_java6.entity.NguoiDung;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Optional;

public interface NguoiDungService {
    // 1. Phương thức cần thiết để lấy thông tin người dùng cho CheckoutController
    Optional<NguoiDung> findByTenDangNhap(String tenDangNhap);

    // 2. Các phương thức CRUD cơ bản (thường cần có)
    NguoiDung save(NguoiDung nguoiDung);
    Optional<NguoiDung> findById(Integer id);
    List<NguoiDung> findAll();
    void deleteById(Integer id);
    List<NguoiDung> searchByHoTen(String hoTen); // Thêm dòng này

    //quên mật khẩu
    void sendOtp(String email, HttpSession session);
    void updateAccount(String email, String newUsername, String newPassword);
}
