package com.poly.assaiment_java6.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal; // Cần import

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    // Sửa Long thành Integer (hoặc giữ Long và đảm bảo Entity là Long)
    // Tốt nhất là sử dụng kiểu của Entity: Integer
    private Integer idSanPham;

    private String tenSanPham;

    // Sửa Double thành BigDecimal (khớp với Entity)
    private BigDecimal giaBan;

    private String duongDanAnh;
    private String tenDanhMuc;
}