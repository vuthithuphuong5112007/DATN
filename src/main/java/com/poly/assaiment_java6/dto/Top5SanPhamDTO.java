package com.poly.assaiment_java6.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Top5SanPhamDTO {
    private String tenSanPham;
    private Long tongSoLuong;
}
