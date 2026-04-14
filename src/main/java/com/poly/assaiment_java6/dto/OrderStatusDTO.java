package com.poly.assaiment_java6.dto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
// Trạng thái đơn hàng (Cho phần biểu đồ của Dashboard trong admin.html)
public class OrderStatusDTO {
    private String status;
    private Long count;

    // Constructor để JPA gọi vào
    public OrderStatusDTO(String status, Long count) {
        this.status = status;
        this.count = count;
    }
}
