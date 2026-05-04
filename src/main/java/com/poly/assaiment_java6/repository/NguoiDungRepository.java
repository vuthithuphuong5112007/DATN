package com.poly.assaiment_java6.repository;

import com.poly.assaiment_java6.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Integer> {
    Optional<NguoiDung> findByTenDangNhap(String tenDangNhap);
    Optional<NguoiDung> findByEmail(String email);
    List<NguoiDung> findByVaiTro(String vaiTro);
    boolean existsByTenDangNhap(String tenDangNhap);
    // Thêm dòng này để tìm kiếm theo tên
    // Tìm tất cả những người mà tên có chứa cụm từ (không phân biệt hoa thường)
    List<NguoiDung> findByHoTenContainingIgnoreCase(String hoTen);

}
