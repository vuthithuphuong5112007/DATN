package com.poly.assaiment_java6.controller;

import com.poly.assaiment_java6.dto.CartItemDTO;
import com.poly.assaiment_java6.entity.ChiTietDonHang;
import com.poly.assaiment_java6.entity.DonHang;
import com.poly.assaiment_java6.entity.NguoiDung;
import com.poly.assaiment_java6.entity.SanPham;
import com.poly.assaiment_java6.service.ChiTietDonHangService;
import com.poly.assaiment_java6.service.DonHangService;
import com.poly.assaiment_java6.service.NguoiDungService;
import com.poly.assaiment_java6.service.SanPhamService;
import com.poly.assaiment_java6.utils.SessionCartUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.math.BigDecimal;
import java.util.*;

@Controller
public class CheckOutController {
    @Autowired private SanPhamService sanPhamService;
    @Autowired private NguoiDungService nguoiDungService;
    @Autowired private DonHangService donHangService;
    @Autowired private ChiTietDonHangService chiTietDonHangService;
    /**
     * Xử lý nút "Thanh toán ngay" từ trang chi tiết sản phẩm.
     */
    @GetMapping("checkout/buy-now")
    public String buyNow(@RequestParam("productId") Integer id,
                         @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
                         HttpSession session) {

        // 1. Lấy thông tin sản phẩm
        SanPham product = sanPhamService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));

        if (quantity < 1) {
            quantity = 1;
        }

        // 2. Tạo CartItemDTO tạm thời (chỉ có 1 sản phẩm)
        CartItemDTO item = new CartItemDTO();
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setPrice(product.getGiaBan());

        List<CartItemDTO> tempCart = new ArrayList<>();
        tempCart.add(item);

        // 3. Lưu danh sách tạm thời này vào Session
        SessionCartUtils.saveItems(session, tempCart);

        return "redirect:/checkout";
    }

    @Transactional
    @PostMapping("/checkout/place-order")
    public String placeOrder(
            @ModelAttribute("donHang") DonHang donHangFromForm, // Nhận PTTT từ radio button
            @RequestParam("address") String address,
            @RequestParam(value = "productIds", required = false) List<Integer> productIds,
            @RequestParam(value = "quantities", required = false) List<Integer> quantities,
            Authentication authentication,
            HttpSession session,
            Model model) {

        try {
            // 1. Gán thông tin khách hàng và địa chỉ
            if (authentication != null && authentication.isAuthenticated()) {
                NguoiDung nguoiDat = nguoiDungService.findByTenDangNhap(authentication.getName()).orElse(null);
                donHangFromForm.setNguoiDung(nguoiDat);
            }
            donHangFromForm.setDiaChiGiaoHang(address);
            donHangFromForm.setNgayDatHang(new Date());
            donHangFromForm.setTrangThaiDonHang("PENDING");
            donHangFromForm.setTongTien(BigDecimal.ZERO);

            // 2. Lưu đơn hàng để có ID
            DonHang savedOrder = donHangService.save(donHangFromForm);
            BigDecimal sumPrice = BigDecimal.ZERO;

            // 3. Lưu chi tiết sản phẩm
            if (productIds != null) {
                for (int i = 0; i < productIds.size(); i++) {
                    SanPham sp = sanPhamService.findById(productIds.get(i)).orElse(null);
                    if (sp != null) {
                        ChiTietDonHang ct = new ChiTietDonHang();
                        ct.setDonHang(savedOrder);
                        ct.setSanPham(sp);
                        ct.setQuantity(quantities.get(i));
                        ct.setPriceAtOrder(sp.getGiaBan());
                        chiTietDonHangService.save(ct);
                        sumPrice = sumPrice.add(sp.getGiaBan().multiply(new BigDecimal(quantities.get(i))));
                    }
                }
            }

            // 4. Cập nhật tổng tiền cuối cùng
            savedOrder.setTongTien(sumPrice);
            donHangService.save(savedOrder);

            // 5. Xử lý chuyển hướng
            session.removeAttribute("shoppingCart");

            // Kiểm tra nếu người dùng chọn TRANSFER (Chuyển khoản)
            if ("TRANSFER".equalsIgnoreCase(savedOrder.getPhuongThucThanhToan())) {
                model.addAttribute("order", savedOrder);
                return "huongdanchuyenkhoan"; // Mở trang có mã QR
            }

            return "redirect:/orders"; // Nếu là COD thì về lịch sử

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/cart?error=system_error";
        }
    }

    @PostMapping("/checkout/submit")
    public String datHang(@ModelAttribute("order") DonHang order, Model model) {
        // 1. Lưu đơn hàng vào Database trước (Để có ID đơn hàng)
        order.setNgayDatHang(new Date());
        order.setTrangThaiDonHang("PENDING");
        donHangService.save(order); // Giả sử hàm này lưu và tự cập nhật ID vào object order

        System.out.println("DEBUG: Phuong thuc nhan duoc la: [" + order.getPhuongThucThanhToan() + "]");
        // 2. Kiểm tra phương thức thanh toán
        if ("TRANSFER".equals(order.getPhuongThucThanhToan())) {
            // Truyền thông tin đơn hàng sang trang hướng dẫn để lấy mã ID và tổng tiền
            model.addAttribute("order", order);
            return "huongdanchuyenkhoan"; // Tên file HTML của bạn
        }

        // Nếu là COD thì sang trang thành công luôn
        return "redirect:/checkout/success";
    }

    /**
     * Phương thức hiển thị trang thông báo đặt hàng thành công
     * URL: GET /checkout/order-success
     */
    @GetMapping("checkout/order-success")
    public String orderSuccess() {
        return "order-success"; // Trả về file order-success.html
    }

}
