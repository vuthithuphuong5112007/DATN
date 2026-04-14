package com.poly.assaiment_java6.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// Doanh thu hàng tháng
public class MonthlyRevenueDTO {
    private Integer month;
    private Double revenue;
    public MonthlyRevenueDTO(Integer month, Object revenue) {
        this.month = month;
        // Ép kiểu từ bất kỳ số nào (BigDecimal, Long, Double) về Double để vẽ biểu đồ
        this.revenue = (revenue != null) ? ((Number) revenue).doubleValue() : 0.0;
    }
}
