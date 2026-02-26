package com.poly.assaiment_java6.controller;

import com.poly.assaiment_java6.entity.NguoiDung;
import com.poly.assaiment_java6.service.NguoiDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin/khachhang")
public class AdminKhachHangController {
    @Autowired
    private NguoiDungService nguoiDungService;

    // 1. HIỂN THỊ DANH SÁCH VÀ FORM (READ)
    @GetMapping
    public String viewKhachHangPage(Model model) {
        // Lấy tất cả người dùng cho bảng
        model.addAttribute("danhSachNguoiDung", nguoiDungService.findAll());

        // Tạo đối tượng NguoiDung mới cho Form (Thêm mới)
        if (!model.containsAttribute("nguoiDung")) {
            // Quan trọng: Tạo NguoiDung mới để tránh lỗi th:field
            NguoiDung newNguoiDung = new NguoiDung();
            // Đặt vai trò mặc định cho form
            newNguoiDung.setVaiTro("USER");
            model.addAttribute("nguoiDung", newNguoiDung);
        }

        // Cần danh sách vai trò để hiển thị Select Box
        model.addAttribute("roles", new String[]{"USER", "ADMIN", "OWNER"});

        return "admin/quan-ly-khach-hang";
    }

    // 2. XỬ LÝ LƯU (THÊM MỚI / CẬP NHẬT)
    @PostMapping("/save")
    public String saveKhachHang(@ModelAttribute("nguoiDung") NguoiDung nguoiDung,
                                RedirectAttributes redirectAttributes) {
        try {
            // LƯU Ý: Nếu đây là thêm mới, bạn cần xử lý mã hóa mật khẩu trong Service/Logic

            // Nếu ID tồn tại, đây là cập nhật. Nếu không, là thêm mới.
            nguoiDungService.save(nguoiDung);
            redirectAttributes.addFlashAttribute("message", "Lưu thông tin khách hàng thành công!");
            return "redirect:/admin/khachhang";
        } catch (Exception e) {
            e.printStackTrace();
            // Nếu có lỗi, đẩy lại đối tượng lỗi để giữ lại dữ liệu form
            redirectAttributes.addFlashAttribute("error", "Lưu thất bại: " + e.getMessage());
            redirectAttributes.addFlashAttribute("nguoiDung", nguoiDung);
            return "redirect:/admin/khachhang";
        }
    }

    // 3. SỬA (NẠP DỮ LIỆU CŨ)
    @GetMapping("/edit/{id}")
    public String editKhachHang(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        Optional<NguoiDung> nguoiDungOpt = nguoiDungService.findById(id);
        if (nguoiDungOpt.isPresent()) {
            // Dùng addFlashAttribute để đối tượng này được chuyển sang /admin/khachhang (GET)
            redirectAttributes.addFlashAttribute("nguoiDung", nguoiDungOpt.get());
        }
        return "redirect:/admin/khachhang";
    }

    // 4. XÓA
    @GetMapping("/delete/{id}")
    public String deleteKhachHang(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            nguoiDungService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Xóa khách hàng thành công!");
        } catch (Exception e) {
            // Xử lý nếu người dùng này đang có đơn hàng liên kết
            redirectAttributes.addFlashAttribute("error", "Không thể xóa. Khách hàng này có liên kết dữ liệu!");
        }
        return "redirect:/admin/khachhang";
    }
}
