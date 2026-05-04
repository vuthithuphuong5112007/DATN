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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
            @RequestParam("address") String address,
            @RequestParam(value = "productIds", required = false) List<Integer> productIds,
            @RequestParam(value = "quantities", required = false) List<Integer> quantities,
            Authentication authentication,
            HttpSession session,
            Model model) {

        // Kiểm tra dữ liệu đầu vào
        if (productIds == null || productIds.isEmpty() || quantities == null) {
            System.out.println("LỖI: Không nhận được danh sách sản phẩm từ Form!");
            return "redirect:/cart";
        }

        try {
            DonHang donHang = new DonHang();

            // Gán người dùng
            if (authentication != null && authentication.isAuthenticated()) {
                NguoiDung nguoiDat = nguoiDungService.findByTenDangNhap(authentication.getName()).orElse(null);
                donHang.setNguoiDung(nguoiDat);
            }

            donHang.setDiaChiGiaoHang(address);
            donHang.setNgayDatHang(new Date());
            donHang.setTrangThaiDonHang("PENDING");
            donHang.setTongTien(BigDecimal.ZERO);

            // LƯU ĐƠN HÀNG TRƯỚC
            DonHang savedOrder = donHangService.save(donHang);
            BigDecimal tinhTongTien = BigDecimal.ZERO;

            // LẶP VÀ KIỂM TRA TỪNG SẢN PHẨM
            for (int i = 0; i < productIds.size(); i++) {
                Integer pId = productIds.get(i);
                Integer qty = quantities.get(i);

                // Tìm sản phẩm trong DB
                Optional<SanPham> spOpt = sanPhamService.findById(pId);
                if (spOpt.isPresent()) {
                    SanPham sp = spOpt.get();
                    ChiTietDonHang chiTiet = new ChiTietDonHang();
                    chiTiet.setDonHang(savedOrder);
                    chiTiet.setSanPham(sp);
                    chiTiet.setQuantity(qty);
                    chiTiet.setPriceAtOrder(sp.getGiaBan());

                    chiTietDonHangService.save(chiTiet);

                    BigDecimal subTotal = sp.getGiaBan().multiply(new BigDecimal(qty));
                    tinhTongTien = tinhTongTien.add(subTotal);
                } else {
                    System.out.println("CẢNH BÁO: Không tìm thấy sản phẩm ID: " + pId);
                }
            }

            // Cập nhật lại tổng tiền
            savedOrder.setTongTien(tinhTongTien);
            donHangService.save(savedOrder);

            // Xóa sạch giỏ hàng
            session.removeAttribute("shoppingCart");
            session.removeAttribute("tempCheckoutItems");

            return "redirect:/orders";

        } catch (Exception e) {
            System.out.println("LỖI THẬT SỰ ĐÂY NÀY: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/cart?error=system_error";
        }
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
