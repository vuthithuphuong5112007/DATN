package com.poly.assaiment_java6.repository;

import com.poly.assaiment_java6.entity.ChiTietDonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiTietDonHangRepository extends JpaRepository<ChiTietDonHang, Integer>{
    List<ChiTietDonHang> findByDonHang_IdDonHang(Integer idDonHang);
    @Query("SELECT c.sanPham.tenSanPham, SUM(c.quantity) " +
            "FROM ChiTietDonHang c " +
            "GROUP BY c.sanPham.tenSanPham " +
            "ORDER BY SUM(c.quantity) DESC")
    List<Object[]> findTopSellingProducts(org.springframework.data.domain.Pageable pageable);
}
