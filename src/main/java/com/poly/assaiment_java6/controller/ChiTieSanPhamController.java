package com.poly.assaiment_java6.controller;

import com.poly.assaiment_java6.entity.Danhgia;
import com.poly.assaiment_java6.entity.SanPham;
import com.poly.assaiment_java6.repository.ReviewRepository;
import com.poly.assaiment_java6.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
public class ChiTieSanPhamController {

    @Autowired
    private SanPhamService productService;

    @Autowired
    private ReviewRepository reviewRepository; // Thêm Repository để xử lý đánh giá

    // 1. Hiển thị trang giao diện Chi tiết sản phẩm
    @GetMapping("/products/detail/{id}")
    public String showProductDetail(@PathVariable("id") Integer id, Model model) {
        SanPham product = productService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));

        model.addAttribute("product", product);

        // Giả sử bạn có logic check admin ở đây, truyền xuống để hiện nút xóa
        // Ví dụ tạm thời để true để bạn test, sau này bạn thay bằng logic check Role thật nhé
        model.addAttribute("isAdmin", true);

        return "chitietsanpham";
    }



    // 2. Lấy danh sách đánh giá của sản phẩm (Dùng Long cho productId vì Entity Danhgia dùng Long)
    @ResponseBody
    @GetMapping("/products/detail/{productId}/reviews")
    public List<Danhgia> getReviewsByProduct(@PathVariable("productId") Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    // 3. Thêm đánh giá mới
    @ResponseBody
    @PostMapping("/products/detail/reviews")
    public Danhgia createReview(@RequestBody Danhgia danhgia) {
        danhgia.setCreatedAt(LocalDateTime.now());
        return reviewRepository.save(danhgia);
    }

    // 4. Xóa đánh giá (Dành cho Admin)
    @ResponseBody
    @DeleteMapping("/products/detail/reviews/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable("id") Long id) {
        try {
            reviewRepository.deleteById(id);
            return ResponseEntity.ok("Xóa thành công");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi xóa");
        }
    }
}