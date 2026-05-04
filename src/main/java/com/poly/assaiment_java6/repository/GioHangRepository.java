package com.poly.assaiment_java6.repository;

import com.poly.assaiment_java6.entity.GioHang;
import com.poly.assaiment_java6.entity.NguoiDung;
import com.poly.assaiment_java6.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GioHangRepository extends JpaRepository<GioHang, Integer> {
    // Tìm giỏ hàng của một người dùng cụ thể
    List<GioHang> findByNguoiDung(NguoiDung nguoiDung);

    // Kiểm tra xem sản phẩm này đã có trong giỏ của người này chưa để cộng dồn số lượng
    Optional<GioHang> findByNguoiDungAndSanPham(NguoiDung nguoiDung, SanPham sanPham);

    // Xóa toàn bộ giỏ hàng sau khi thanh toán xong
    void deleteByNguoiDung(NguoiDung nguoiDung);
}
