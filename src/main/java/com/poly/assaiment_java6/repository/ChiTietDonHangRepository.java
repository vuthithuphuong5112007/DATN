package com.poly.assaiment_java6.repository;

import com.poly.assaiment_java6.dto.Top5SanPhamDTO;
import com.poly.assaiment_java6.entity.ChiTietDonHang;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiTietDonHangRepository extends JpaRepository<ChiTietDonHang, Integer>{
    @Query("SELECT NEW com.poly.assaiment_java6.dto.Top5SanPhamDTO(c.sanPham.tenSanPham, SUM(c.quantity)) " +
            "FROM ChiTietDonHang c " +
            "GROUP BY c.sanPham.tenSanPham " +
            "ORDER BY SUM(c.quantity) DESC")
    List<Top5SanPhamDTO> findTopSellingProducts(Pageable pageable);
}
