package com.poly.assaiment_java6.controller;

import com.poly.assaiment_java6.entity.DonHang;
import com.poly.assaiment_java6.service.DonHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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

        DonHang order = donHangService.findById(orderId).orElse(null);

        if (order == null) {
            // Trả về một fragment lỗi đơn giản (fragment này cũng cần nằm trong order-list.html nếu bạn muốn)
            // Hiện tại, tạm thời sử dụng logic kiểm tra lỗi trong JS
            return null; // Hoặc trả về một trang 404 tùy chỉnh
        }

        model.addAttribute("order", order);

        // 🚀 SỬA: CHỈ ĐỊNH ĐÚNG FILE HTML ĐANG CHỨA FRAGMENT
        // Tên file của bạn là "admin/order-list"
        // Fragment là "detailContent"
        return "admin/order-list :: detailContent";
    }
}
