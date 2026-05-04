package com.poly.assaiment_java6.controller;

import com.poly.assaiment_java6.entity.Danhgia;
import com.poly.assaiment_java6.entity.SanPham;
import com.poly.assaiment_java6.repository.ReviewRepository;
import com.poly.assaiment_java6.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private ReviewRepository reviewRepository;

    // 1. Hiển thị trang giao diện Chi tiết sản phẩm (ĐÃ GỘP VÀ FIX CHUẨN)
    @GetMapping("/products/detail/{id}")
    public String showProductDetail(@PathVariable("id") Integer id, Model model) {
        // Lấy sản phẩm từ database
        SanPham product = productService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
        model.addAttribute("product", product);

        // Kiểm tra quyền Admin từ Spring Security (Không gán cứng true nữa)
        boolean isAdmin = false;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            // Kiểm tra xem User này có mang cờ ROLE_ADMIN hoặc ADMIN không
            isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));
        }

        // Gửi quyền chuẩn xác xuống HTML
        model.addAttribute("isAdmin", isAdmin);

        return "chitietsanpham";
    }



    // 4. Xóa đánh giá (ĐÃ THÊM BẢO MẬT CHẶN Ở SERVER)
    @ResponseBody
    @DeleteMapping("/products/detail/reviews/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable("id") Long id) {
        // Chặn luông ở Back-end: Kể cả hack được giao diện cũng không xóa được
        boolean isAdmin = false;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));
        }

        if (!isAdmin) {
            return ResponseEntity.status(403).body("Lỗi: Chỉ Admin mới được xóa!");
        }

        try {
            reviewRepository.deleteById(id);
            return ResponseEntity.ok("Xóa thành công");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi xóa");
        }
    }
}