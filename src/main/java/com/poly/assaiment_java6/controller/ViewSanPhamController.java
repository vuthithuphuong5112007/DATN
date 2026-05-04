package com.poly.assaiment_java6.controller;

import com.poly.assaiment_java6.dto.ProductDTO;
import com.poly.assaiment_java6.entity.SanPham;
import com.poly.assaiment_java6.repository.SanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ViewSanPhamController {
    @Autowired
    private SanPhamRepository sanPhamRepository;

    @GetMapping("/search-name")
    @ResponseBody // <--- QUAN TRỌNG: Báo cho Spring biết hàm này trả về JSON, không phải HTML
    public ResponseEntity<List<SanPham>> searchByName(@RequestParam("keyword") String keyword) {

        // Gọi Service (Lưu ý: dùng đúng tên biến sanPhamService, không phải productService)
        List<SanPham> products = sanPhamRepository.searchProductsByName(keyword);

        // Trả kết quả về cho giao diện (View) dưới định dạng JSON
        return ResponseEntity.ok(products);
    }
    // NHẬN THÊM THAM SỐ @RequestParam("q")
    @GetMapping("/sanpham")
    public String showProductPage(Model model,
                                  @RequestParam(value = "q", required = false) String keyword,
                                  // Đã sửa 'size' từ 12 về 8 theo yêu cầu của bạn
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "8") int size) {

        List<ProductDTO> sanPhams;
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDTO> sanPhamPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            // Đảm bảo Repository có thể trả về Page (không List)
            sanPhamPage = sanPhamRepository.findProductsByKeyword("%" + keyword + "%", pageable);
        } else {
            // Đảm bảo Repository có thể trả về Page (không List)
            sanPhamPage = sanPhamRepository.findAllActive(pageable);
        }

        // 1. CHẮC CHẮN ĐÃ TRUYỀN Page<ProductDTO>
        model.addAttribute("sanPhamPage", sanPhamPage);

        // 2. CHẮC CHẮN ĐÃ TRUYỀN List<ProductDTO> để vòng lặp th:each hoạt động
        model.addAttribute("sanPhams", sanPhamPage.getContent());
        model.addAttribute("currentKeyword", keyword);

        return "sanpham";
    }

    @GetMapping("/shop")
    public String shopPage(
            @RequestParam(value = "q", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "12") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDTO> sanPhamPage;

        // Kiểm tra nếu có từ khóa tìm kiếm
        if (keyword != null && !keyword.isEmpty()) {
            // Sử dụng hàm có sẵn trong Repo của bạn: findProductsByKeyword
            // Thêm dấu % để tìm kiếm LIKE
            sanPhamPage = sanPhamRepository.findProductsByKeyword("%" + keyword + "%", pageable);
        } else {
            // Sử dụng hàm hiển thị mặc định có sẵn trong Repo của bạn
            sanPhamPage = sanPhamRepository.findAllActive(pageable);
        }

        // Đẩy dữ liệu sang HTML theo đúng tên biến bạn đang dùng trong file shop.html
        model.addAttribute("sanPhams", sanPhamPage.getContent());
        model.addAttribute("sanPhamPage", sanPhamPage);
        model.addAttribute("currentKeyword", keyword);

        return "shop";
    }
}
