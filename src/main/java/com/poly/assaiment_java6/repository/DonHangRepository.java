package com.poly.assaiment_java6.repository;

import com.poly.assaiment_java6.dto.MonthlyRevenueDTO;
import com.poly.assaiment_java6.dto.OrderStatusDTO;
import com.poly.assaiment_java6.entity.DonHang;
import com.poly.assaiment_java6.entity.NguoiDung;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface DonHangRepository extends JpaRepository<DonHang, Integer>{
    List<DonHang> findByNguoiDungOrderByNgayDatHangDesc(NguoiDung nguoiDung);
    // Cách 1: Dùng JPQL (Truy vấn trên Entity)
    @Query("SELECT SUM(d.tongTien) FROM DonHang d WHERE d.trangThaiDonHang = 'COMPLETED'")
    BigDecimal getTotalRevenue();
    // Spring sẽ tự hiểu là: SELECT COUNT(*) FROM DonHang WHERE trangThaiDonHang = ?
    long countByTrangThaiDonHang(String trangThai);

    @Query("SELECT d FROM DonHang d ORDER BY d.idDonHang DESC")
    List<DonHang> findTop5RecentOrders(Pageable pageable);

    // DonHangRepository.java
    @Query("SELECT NEW com.poly.assaiment_java6.dto.MonthlyRevenueDTO(DAY(d.ngayDatHang), SUM(d.tongTien)) " +
            "FROM DonHang d " +
            "WHERE MONTH(d.ngayDatHang) = :month AND YEAR(d.ngayDatHang) = :year AND d.trangThaiDonHang = 'COMPLETED' " +
            "GROUP BY DAY(d.ngayDatHang) " +
            "ORDER BY DAY(d.ngayDatHang)")
    List<MonthlyRevenueDTO> getDailyRevenue(@Param("month") int month, @Param("year") int year);

    @Query("SELECT NEW com.poly.assaiment_java6.dto.OrderStatusDTO(d.trangThaiDonHang, COUNT(d)) " +
            "FROM DonHang d " +
            "GROUP BY d.trangThaiDonHang")
    List<OrderStatusDTO> countOrdersByStatus();
}
