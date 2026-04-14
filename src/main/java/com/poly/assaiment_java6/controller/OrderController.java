package com.poly.assaiment_java6.controller;

import com.poly.assaiment_java6.entity.DonHang;
import com.poly.assaiment_java6.entity.NguoiDung;
import com.poly.assaiment_java6.service.DonHangService;
import com.poly.assaiment_java6.service.NguoiDungService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class OrderController {
    @Autowired
    private DonHangService donHangService;

    @Autowired
    private NguoiDungService nguoiDungService;

    @GetMapping("/orders")
    public String listOrders(Model model, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            // Chuyển hướng đến trang đăng nhập nếu chưa đăng nhập
            return "redirect:/login";
        }

        try {
            String username = authentication.getName();

            // 1. Tìm thông tin người dùng từ DB
            Optional<NguoiDung> nguoiDungOpt = nguoiDungService.findByTenDangNhap(username);

            if (nguoiDungOpt.isPresent()) {
                NguoiDung user = nguoiDungOpt.get();

                // 2. Lấy danh sách đơn hàng của người dùng đó (Giả sử có phương thức findByNguoiDung)
                List<DonHang> orders = donHangService.findByNguoiDung(user);

                // 3. Truyền danh sách đơn hàng sang View
                model.addAttribute("orders", orders);
            } else {
                model.addAttribute("orders", Collections.emptyList());
            }

        } catch (Exception e) {
            // Xử lý lỗi nếu có
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi tải lịch sử đơn hàng.");
            model.addAttribute("orders", Collections.emptyList());
        }

        return "orders"; // Trả về file orders.html
    }

    // --- PHƯƠNG THỨC 2: Chi tiết đơn hàng (PHẢI TÁCH RIÊNG RA THẾ NÀY) ---
    @GetMapping("/orders/detail/{id}")
    public String getOrderDetail(@PathVariable("id") Integer id, Model model) {
        // 1. Tìm đơn hàng theo ID
        Optional<DonHang> order = donHangService.findById(id);

        if (order.isPresent()) {
            model.addAttribute("order", order.get());
            return "orders-detail"; // File HTML chi tiết đơn hàng (ví dụ: order.html)
        } else {
            return "redirect:/orders"; // Không thấy đơn hàng thì quay về danh sách
        }
    }
}
