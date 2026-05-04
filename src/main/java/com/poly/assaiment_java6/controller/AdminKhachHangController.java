package com.poly.assaiment_java6.controller;

import com.poly.assaiment_java6.entity.NguoiDung;
import com.poly.assaiment_java6.service.NguoiDungService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
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
    public String saveKhachHang(@Valid @ModelAttribute("nguoiDung") NguoiDung nguoiDung,
                                BindingResult result, // Thêm BindingResult để hứng lỗi validation
                                Model model,          // Dùng Model thay vì RedirectAttributes khi có lỗi
                                RedirectAttributes redirectAttributes) {

        // BƯỚC 1: Kiểm tra lỗi dữ liệu (trống, sai định dạng...)
        if (result.hasErrors()) {
            // Nạp lại các dữ liệu cần thiết cho giao diện (như danh sách roles, danh sách người dùng)
            model.addAttribute("roles", List.of("ADMIN", "USER")); // Thay bằng logic lấy roles của bạn
            model.addAttribute("danhSachNguoiDung", nguoiDungService.findAll());

            // TRẢ VỀ TRỰC TIẾP VIEW (Không redirect) để Thymeleaf hiển thị lỗi dưới từng input
            return "admin/quan-ly-khach-hang";
        }

        try {
            nguoiDungService.save(nguoiDung);
            redirectAttributes.addFlashAttribute("message", "Lưu thông tin khách hàng thành công!");
            return "redirect:/admin/khachhang"; // Thành công thì mới redirect
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            model.addAttribute("roles", List.of("ADMIN", "USER"));
            model.addAttribute("danhSachNguoiDung", nguoiDungService.findAll());
            return "admin/quan-ly-khach-hang";
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
            // Bước 1: Tìm người dùng theo ID
            NguoiDung nd = nguoiDungService.findById(id).orElse(null);

            // Bước 2: Kiểm tra nếu là tài khoản OWNER thì không cho xóa
            if (nd != null && "OWNER".equalsIgnoreCase(nd.getVaiTro())) {
                redirectAttributes.addFlashAttribute("error", "Không thể xóa tài khoản OWNER (Quản trị viên tối cao)!");
                return "redirect:/admin/khachhang";
            }

            // Bước 3: Nếu không phải OWNER thì mới thực hiện xóa
            nguoiDungService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Xóa khách hàng thành công!");

        } catch (Exception e) {
            // Xử lý nếu người dùng này đang có đơn hàng liên kết (Lỗi khóa ngoại)
            redirectAttributes.addFlashAttribute("error", "Không thể xóa. Khách hàng này đã có lịch sử mua hàng hoặc dữ liệu liên kết!");
        }
        return "redirect:/admin/khachhang";
    }

    //tìm kiếm khách hàng
    @GetMapping("/search")
    public String searchKhachHang(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<NguoiDung> ketQuaTimKiem;

        // Nếu người dùng không nhập gì hoặc chỉ nhập khoảng trắng
        if (keyword == null || keyword.trim().isEmpty()) {
            ketQuaTimKiem = nguoiDungService.findAll(); // Trả về tất cả danh sách
        } else {
            // Tìm kiếm gần đúng (chứa cụm từ) và không phân biệt hoa thường
            ketQuaTimKiem = nguoiDungService.searchByHoTen(keyword.trim());

            // Nếu tìm kiếm mà thực sự không có ai khớp cụm từ đó
            if (ketQuaTimKiem.isEmpty()) {
                model.addAttribute("error", "Không tìm thấy khách hàng nào có tên chứa: " + keyword);
            }
        }

        model.addAttribute("danhSachNguoiDung", ketQuaTimKiem);
        model.addAttribute("keyword", keyword); // Giữ lại chữ đã gõ trên ô input

        // Nạp lại dữ liệu cho Form
        if (!model.containsAttribute("nguoiDung")) {
            NguoiDung newNguoiDung = new NguoiDung();
            newNguoiDung.setVaiTro("USER");
            model.addAttribute("nguoiDung", newNguoiDung);
        }
        model.addAttribute("roles", new String[]{"USER", "ADMIN", "OWNER"});

        return "admin/quan-ly-khach-hang";
    }
}
