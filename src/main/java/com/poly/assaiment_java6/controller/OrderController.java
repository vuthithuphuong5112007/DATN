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
}
