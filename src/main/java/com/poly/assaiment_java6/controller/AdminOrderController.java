package com.poly.assaiment_java6.controller;

import com.poly.assaiment_java6.entity.DonHang;
import com.poly.assaiment_java6.service.DonHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/donhang")
public class AdminOrderController {
    @Autowired
    private DonHangService donHangService;

    // --- 1. HIỂN THỊ DANH SÁCH TẤT CẢ ĐƠN HÀNG ---
    @GetMapping
    public String listAllOrders(Model model) {
        // Lấy tất cả đơn hàng từ database (cần một phương thức findAll() trong Service)
        List<DonHang> orders = donHangService.findAll();

        model.addAttribute("orders", orders);
        model.addAttribute("pageTitle", "Quản lý Đơn hàng");
        return "admin/order-list"; // Trả về file admin/order-list.html
    }


    // --- 3. LẤY FRAGMENT CHI TIẾT ĐƠN HÀNG QUA AJAX ---
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
    // Đừng quên hàm này để nút "Cập nhật" trong phần chi tiết hoạt động được nhé
    @PostMapping("/update-status/{id}")
    public String updateStatus(@PathVariable("id") Integer id,
                               @RequestParam("newStatus") String newStatus,
                               RedirectAttributes redirectAttributes) { // Thêm RedirectAttributes để báo lỗi

        // Sử dụng .orElse(null) bạn đã fix thành công để tránh lỗi Optional
        DonHang order = donHangService.findById(id).orElse(null);

        if (order != null) {
            // KIỂM TRA: Nếu trạng thái hiện tại là COMPLETED hoặc CANCELLED thì không cho sửa
            if ("COMPLETED".equals(order.getTrangThaiDonHang()) || "CANCELLED".equals(order.getTrangThaiDonHang())) {
                redirectAttributes.addFlashAttribute("error", "Đơn hàng đã chốt (Hoàn tất/Hủy), không thể thay đổi trạng thái nữa!");
                return "redirect:/admin/donhang";
            }

            // Nếu hợp lệ thì mới cập nhật
            order.setTrangThaiDonHang(newStatus);
            donHangService.save(order);
            redirectAttributes.addFlashAttribute("message", "Cập nhật trạng thái đơn hàng #" + id + " thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng!");
        }

        return "redirect:/admin/donhang";
    }
}
