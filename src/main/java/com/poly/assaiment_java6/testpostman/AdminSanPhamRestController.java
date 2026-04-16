package com.poly.assaiment_java6.testpostman;

import com.poly.assaiment_java6.entity.SanPham;

import com.poly.assaiment_java6.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/api/products")
public class AdminSanPhamRestController {
    @Autowired
    private SanPhamService sanPhamService;

    @GetMapping
    public List<SanPham> getAllProducts() {
        // Trả về toàn bộ danh sách sản phẩm nước hoa dưới dạng JSON
        return sanPhamService.getAllSanPhams();
    }
}
