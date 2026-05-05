package com.poly.assaiment_java6.controller;

import com.poly.assaiment_java6.entity.DonHang;
import com.poly.assaiment_java6.entity.NguoiDung;
import com.poly.assaiment_java6.repository.DonHangRepository;
import com.poly.assaiment_java6.service.DonHangService;
import com.poly.assaiment_java6.service.NguoiDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class OrderController {

    @Autowired
    private DonHangService donHangService;
    @Autowired
    private NguoiDungService nguoiDungService;
    @Autowired
    private DonHangRepository orderRepo; // DonHangRepository của bạn

    // --- CHỈ GIỮ LẠI MỘT CÁI /orders DUY NHẤT ---
    @GetMapping("/orders")
    public String listOrders(Model model, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        try {
            String username = authentication.getName();
            Optional<NguoiDung> nguoiDungOpt = nguoiDungService.findByTenDangNhap(username);

            if (nguoiDungOpt.isPresent()) {
                NguoiDung user = nguoiDungOpt.get();

                // Lấy danh sách đơn hàng và sắp xếp mới nhất lên đầu
                // Nếu donHangService chưa có hàm này, bạn có thể dùng orderRepo trực tiếp
                List<DonHang> orders = orderRepo.findByNguoiDungOrderByNgayDatHangDesc(user);

                model.addAttribute("orders", orders);
            } else {
                model.addAttribute("orders", Collections.emptyList());
            }

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi tải lịch sử đơn hàng.");
            model.addAttribute("orders", Collections.emptyList());
        }

        return "orders";
    }

    @GetMapping("/orders/detail/{id}")
    public String getOrderDetail(@PathVariable("id") Integer id, Model model) {
        Optional<DonHang> order = donHangService.findById(id);
        if (order.isPresent()) {
            model.addAttribute("order", order.get());
            return "orders-detail";
        } else {
            return "redirect:/orders";
        }
    }

    // Xử lý hủy đơn hàng (Link GET)
    @GetMapping("/orders/cancel/{id}")
    public String cancelOrderGet(@PathVariable("id") Integer id) {
        Optional<DonHang> orderOpt = donHangService.findById(id);
        if (orderOpt.isPresent()) {
            DonHang order = orderOpt.get();
            if ("PENDING".equals(order.getTrangThaiDonHang())) {
                order.setTrangThaiDonHang("CANCELLED");
                donHangService.save(order);
            }
        }
        return "redirect:/orders/detail/" + id;
    }

    // Xử lý hủy đơn hàng (Form POST có lý do)
    @PostMapping("/orders/cancel")
    public String cancelOrderPost(@RequestParam("id") Integer id, @RequestParam("reason") String reason) {
        Optional<DonHang> orderOpt = donHangService.findById(id);
        if (orderOpt.isPresent()) {
            DonHang order = orderOpt.get();
            if ("PENDING".equals(order.getTrangThaiDonHang())) {
                order.setTrangThaiDonHang("CANCELLED");
                order.setLyDoHuy(reason); // Bật dòng này nếu Entity DonHang có trường lyDoHuy
                donHangService.save(order);
            }
        }
        return "redirect:/orders/detail/" + id;
    }

    // Viết thêm hàm này vào cuối Class để hết lỗi getCurrentUser
    private NguoiDung getCurrentUser(Principal principal) {
        if (principal == null) return null;
        return nguoiDungService.findByTenDangNhap(principal.getName()).orElse(null);
    }
}