package com.poly.assaiment_java6.controller;


import com.poly.assaiment_java6.entity.DanhMuc;
import com.poly.assaiment_java6.service.DanhMucService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/danhmuc")
public class AdminDanhMucController {
    @Autowired
    private DanhMucService danhMucService;

    // 1. HIỂN THỊ DANH SÁCH VÀ FORM (CREATE/READ)
    @GetMapping
    public String viewDanhMucPage(Model model) {
        // Lấy tất cả danh mục cho bảng
        model.addAttribute("listDMs", danhMucService.findAll());

        // Tạo đối tượng DanhMuc mới cho Form (Thêm mới)
        if (!model.containsAttribute("danhMuc")) {
            model.addAttribute("danhMuc", new DanhMuc());
        }

        return "admin/quan-ly-danh-muc"; // Trả về view mới (Bước 4)
    }

    // 2. XỬ LÝ LƯU (THÊM MỚI / CẬP NHẬT)
    @PostMapping("/save")
    public String saveDanhMuc(@ModelAttribute("danhMuc") DanhMuc danhMuc, RedirectAttributes redirectAttributes,
                              HttpServletRequest request) {
        try {
            // --- THÊM DÒNG NÀY ---
            request.setCharacterEncoding("UTF-8");
            // -----------------------

            // Log để kiểm tra giá trị nhận được
            System.out.println("Tên DM sau khi đặt encoding: " + danhMuc.getTenDanhMuc());

            danhMucService.save(danhMuc);
            redirectAttributes.addFlashAttribute("message", "Lưu danh mục thành công!");
            return "redirect:/admin/danhmuc";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error";
        }

    }

    // 3. SỬA (NẠP DỮ LIỆU CŨ)
    @GetMapping("/edit/{id}")
    public String editDanhMuc(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        DanhMuc danhMuc = danhMucService.getById(id);
        if (danhMuc != null) {
            redirectAttributes.addFlashAttribute("danhMuc", danhMuc);
        }
        return "redirect:/admin/danhmuc";
    }

    // 4. XÓA
    @GetMapping("/delete/{id}")
    public String deleteDanhMuc(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            danhMucService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Xóa danh mục thành công!");
        } catch (Exception e) {
            // Xử lý nếu danh mục này đang có sản phẩm liên kết
            redirectAttributes.addFlashAttribute("error", "Không thể xóasss. Danh mục này đang chứa sản phẩm!");
        }
        return "redirect:/admin/danhmuc";
    }
}
