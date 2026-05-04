package com.poly.assaiment_java6.controller;

import com.poly.assaiment_java6.entity.Danhgia;
import com.poly.assaiment_java6.repository.ReviewRepository;
import com.poly.assaiment_java6.repository.SanPhamRepository; // Cần để tìm tên/ảnh sản phẩm
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Quan trọng để đẩy dữ liệu ra HTML
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private SanPhamRepository sanPhamRepository; // Thêm repo này để lấy thông tin sp

    // ==========================================
    // 1. MỞ TRANG REVIEW (Trả về file HTML)
    // Đường dẫn này khớp với th:href="@{/review/product/{id}(id=${item.sanPham.idSanPham})}"
    // ==========================================
    @GetMapping("/review/product/{id}")
    public String showReviewPage(@PathVariable("id") int id, Model model) {
        // Tìm sản phẩm trong DB để trang HTML có thông tin (tên, ảnh) mà hiển thị
        sanPhamRepository.findById(id).ifPresent(product -> {
            model.addAttribute("product", product);
        });
        return "review"; // Phải trả về String (tên file review.html)
    }

    // ==========================================
    // 2. API LẤY DANH SÁCH ĐÁNH GIÁ (Giữ nguyên của bạn)
    // ==========================================
    @ResponseBody
    @GetMapping("/products/detail/{productId}/reviews")
    public List<Danhgia> getReviewsByProduct(@PathVariable("productId") Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    // ==========================================
    // 3. API LƯU ĐÁNH GIÁ MỚI (Cập nhật đường dẫn cho gọn)
    // ==========================================
    @ResponseBody
    @PostMapping("/api/reviews/save") // Khớp với lệnh fetch trong JavaScript ở trang review.html
    public Danhgia createReview(@RequestBody Danhgia danhgia) {
        danhgia.setCreatedAt(LocalDateTime.now());
        return reviewRepository.save(danhgia);
    }
}