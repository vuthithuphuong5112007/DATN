package com.poly.assaiment_java6.controller;

import com.poly.assaiment_java6.entity.DonHang;
import com.poly.assaiment_java6.entity.NguoiDung;
import com.poly.assaiment_java6.service.DonHangService;
import com.poly.assaiment_java6.service.NguoiDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/donhang")
public class AdminOrderController {
    @Autowired
    private DonHangService donHangService;
    @Autowired
    private NguoiDungService nguoiDungService;

    // --- 1. HIỂN THỊ DANH SÁCH TẤT CẢ ĐƠN HÀNG ---
    @GetMapping
    public String listAllOrders(Model model) {
        // Lấy tất cả đơn hàng từ database (cần một phương thức findAll() trong Service)
        List<DonHang> orders = donHangService.findAll();
        model.addAttribute("orders", orders);
        model.addAttribute("pageTitle", "Quản lý Đơn hàng");
        return "admin/order-list"; // Trả về file admin/order-list.html
    }


    // --- 2. LẤY FRAGMENT CHI TIẾT ĐƠN HÀNG QUA AJAX ---
    @GetMapping("/{id}/detail-fragment")
    public String getOrderDetailFragment(@PathVariable("id") Integer orderId, Model model) {
        // Lấy dữ liệu từ Service
        DonHang order = donHangService.findById(orderId).orElse(null);
        if (order == null) {
            return null;
        }
        model.addAttribute("order", order);
        // CHỈ ĐỊNH ĐÚNG FILE: order-list.html
        // Cú pháp: "thư_mục/tên_file :: tên_fragment"
        return "admin/order-list :: detailContent";
    }

    // --- 3. CẬP NHẬT TRẠNG THÁI VÀ GÁN NGƯỜI THỰC HIỆN ---
    @PostMapping("/update-status/{id}")
    public String updateStatus(@PathVariable("id") Integer id,
                               @RequestParam("newStatus") String newStatus,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {

        // 1. Tìm đơn hàng
        DonHang order = donHangService.findById(id).orElse(null);

        if (order != null) {
            // 2. Lấy thông tin Admin đang thao tác từ hệ thống
            String currentUsername = principal.getName();
            NguoiDung adminHienTai = nguoiDungService.findByTenDangNhap(currentUsername).orElse(null);

            // 3. KIỂM TRA: Nếu trạng thái hiện tại là COMPLETED hoặc CANCELLED thì không cho sửa
            if ("COMPLETED".equals(order.getTrangThaiDonHang()) || "CANCELLED".equals(order.getTrangThaiDonHang())) {
                redirectAttributes.addFlashAttribute("error", "Đơn hàng đã chốt, không thể thay đổi trạng thái!");
                return "redirect:/admin/donhang";
            }

            // 4. GÁN DỮ LIỆU: Cập nhật trạng thái VÀ Người thực hiện (Admin/Owner)
            order.setTrangThaiDonHang(newStatus);
            order.setNguoiThucHien(adminHienTai); // Lưu ID của Admin vào id_nguoi_thuc_hien

            // Xử lý lý do hủy nếu có
            if ("CANCELLED".equals(newStatus) && order.getLyDoHuy() == null) {
                String tenAdmin = (adminHienTai != null) ? adminHienTai.getHoTen() : "Admin";
                order.setLyDoHuy("Hủy bởi Quản trị viên: " + tenAdmin);
            }

            donHangService.save(order);
            redirectAttributes.addFlashAttribute("message", "Đã cập nhật trạng thái đơn hàng #" + id + " thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng!");
        }

        return "redirect:/admin/donhang";
    }
}
