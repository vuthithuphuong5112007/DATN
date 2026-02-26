package com.poly.assaiment_java6.dto;

import com.poly.assaiment_java6.entity.SanPham;
import lombok.Data;
import java.math.BigDecimal;


@Data
public class CartItemDTO {
    private SanPham product;
    private Integer quantity; // Số lượng
    private BigDecimal price;  // Giá bán lẻ tại thời điểm hiện tại

    // Tính tổng tiền cho mục hàng này
    public BigDecimal getSubtotal() {
        if (price != null && quantity != null) {
            return price.multiply(new BigDecimal(quantity));
        }
        return BigDecimal.ZERO;
    }
}
