package com.poly.assaiment_java6.controller;

import com.poly.assaiment_java6.entity.SanPham;
import com.poly.assaiment_java6.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.NoSuchElementException;

@Controller
public class ChiTieSanPhamController {
    // Inject service (ví dụ: ProductService)
    @Autowired
    private SanPhamService productService;

    @GetMapping("/products/detail/{id}")
    // Đã sửa: Đổi kiểu ID sang Integer
    public String showProductDetail(@PathVariable("id") Integer id, Model model) {

        // 1. Tìm sản phẩm theo ID
        SanPham product = productService.findById(id)
                // THAY ResourceNotFoundException BẰNG NoSuchElementException
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));

        // 2. Thêm đối tượng sản phẩm vào Model
        model.addAttribute("product", product);

        // 3. Trả về tên file HTML chi tiết sản phẩm
        return "chitietsanpham";
    }
}
