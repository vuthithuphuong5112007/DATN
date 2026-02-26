package com.poly.assaiment_java6.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * DTO chứa tất cả các chỉ số hiệu suất kinh doanh (KPI) tổng hợp
 * để hiển thị trên trang Dashboard quản trị.
 * Sử dụng Lombok.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KpiDto {
    private Long tongNguoiDung;            // Tổng số người dùng hệ thống
    private Long tongSanPham;              // Tổng số sản phẩm hiện có
    private Long tongDonHangThangNay;      // Tổng đơn hàng đã đặt trong tháng hiện tại
    private BigDecimal doanhThuThangNay;    // Tổng doanh thu (tổng tiền các đơn hàng) trong tháng hiện tại
    private BigDecimal giaTriTonKho;
}
