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
    @GetMapping("/checkout")
    public String checkout(
            @RequestParam(value = "items", required = false) String selectedItems,
            Model model,
            HttpSession session,
            Authentication authentication)
    {
        List<CartItemDTO> itemsToCheckout = new ArrayList<>();

        // --- BƯỚC 1: XỬ LÝ LỌC SẢN PHẨM (Chỉ chạy khi có tham số 'items' từ Giỏ hàng) ---
        if (selectedItems != null && !selectedItems.isEmpty()) {
            List<CartItemDTO> allCartItems = SessionCartUtils.getCartItems(session);
            String[] itemPairs = selectedItems.split(",");

            for (String pair : itemPairs) {
                String[] parts = pair.split("-");
                if (parts.length == 2) {
                    try {
                        Integer productId = Integer.parseInt(parts[0]);
                        Integer quantity = Integer.parseInt(parts[1]);

                        Optional<CartItemDTO> itemOpt = allCartItems.stream()
                                .filter(item -> item.getProduct().getIdSanPham().equals(productId))
                                .findFirst();

                        if (itemOpt.isPresent()) {
                            CartItemDTO originalItem = itemOpt.get();
                            CartItemDTO newItem = new CartItemDTO();
                            newItem.setProduct(originalItem.getProduct());
                            newItem.setPrice(originalItem.getPrice());
                            newItem.setQuantity(quantity);

                            itemsToCheckout.add(newItem);
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
            // LƯU GIỎ HÀNG ĐÃ CHỌN VÀO SESSION TẠM THỜI
            SessionCartUtils.saveItems(session, itemsToCheckout);
        } else {
            // --- BƯỚC 2: XỬ LÝ KHI TRUY CẬP TRỰC TIẾP HOẶC TỪ "buyNow" ---
            // Lấy giỏ hàng đã được lưu từ SessionCartUtils.saveItems trước đó (nếu có)
            // Lưu ý: SessionCartUtils.getItems() lấy TEMP_CART_SESSION_KEY
            itemsToCheckout = SessionCartUtils.getItems(session);
        }

        // --- BƯỚC 3: KIỂM TRA & HIỂN THỊ DỮ LIỆU ---
        if (itemsToCheckout == null || itemsToCheckout.isEmpty()) {
            return "redirect:/cart"; // Quay lại giỏ hàng nếu không có gì để thanh toán
        }

        BigDecimal totalPrice = SessionCartUtils.calculateTotal(itemsToCheckout);
        model.addAttribute("cartItems", itemsToCheckout);
        model.addAttribute("totalPrice", totalPrice);


        // --- BƯỚC 4: LẤY THÔNG TIN NGƯỜI DÙNG (Giữ nguyên) ---
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Optional<NguoiDung> nguoiDungOpt = nguoiDungService.findByTenDangNhap(username);
            if (nguoiDungOpt.isPresent()) {
                model.addAttribute("nguoiDung", nguoiDungOpt.get());
            }
        }

        return "checkout";
    }

    @Transactional
    @PostMapping("checkout/place-order")
    public String placeOrder(
            @RequestParam("fullName") String fullName,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam("paymentMethod") String paymentMethod,
            HttpSession session,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        {
            // Lấy danh sách sản phẩm từ Session
            List<CartItemDTO> cartItems = SessionCartUtils.getItems(session);

            if (cartItems == null || cartItems.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Giỏ hàng trống!");
                return "redirect:/cart";
            }
            try {
                // 1. TÌM NGUOI DUNG & TỔNG TIỀN
                NguoiDung nguoiDat = null;
                if (authentication != null && authentication.isAuthenticated()) {
                    // Lấy NguoiDung từ DB
                    // Giả định bạn đã sửa Entity DonHang cho phép nguoiDung = null nếu khách chưa đăng nhập.
                    nguoiDat = nguoiDungService.findByTenDangNhap(authentication.getName()).orElse(null);
                }
                BigDecimal totalPrice = SessionCartUtils.calculateTotal(cartItems);

                // 2. TẠO VÀ LƯU DONHANG (PHẢI LÀ BƯỚC ĐẦU TIÊN)
                DonHang donHang = new DonHang();
                donHang.setNguoiDung(nguoiDat);
                donHang.setNgayDatHang(new Date());
                donHang.setTongTien(totalPrice);
                donHang.setDiaChiGiaoHang(address);
                donHang.setTrangThaiDonHang("PENDING");
                // set các trường khác: phone, email, v.v.

                DonHang donHangVuaLuu = donHangService.save(donHang);

                // 3. LẶP QUA GIỎ HÀNG, TẠO CHI TIẾT ĐƠN HÀNG VÀ GIẢM TỒN KHO
                for (CartItemDTO item : cartItems) {
                    SanPham product = item.getProduct();
                    int orderedQuantity = item.getQuantity();

                    // KIỂM TRA TỒN KHO (Tối thiểu)
                    if (product.getSoLuongTon() < orderedQuantity) {
                        // Nếu tồn kho không đủ, throw exception để @Transactional hủy toàn bộ giao dịch
                        throw new IllegalStateException("Hết hàng: Sản phẩm " + product.getTenSanPham() + " chỉ còn " + product.getSoLuongTon() + " sản phẩm.");
                    }

                    // TẠO VÀ LƯU CHI TIẾT ĐƠN HÀNG
                    ChiTietDonHang ctdh = new ChiTietDonHang();
                    ctdh.setDonHang(donHangVuaLuu);
                    ctdh.setSanPham(product);
                    ctdh.setQuantity(orderedQuantity);
                    ctdh.setPriceAtOrder(item.getPrice());
                    chiTietDonHangService.save(ctdh);

                    // =======================================================
                    // 🚀 BƯỚC GIẢM TỒN KHO (NHẤT THIẾT PHẢI CÓ)
                    // =======================================================
                    int currentStock = product.getSoLuongTon();
                    product.setSoLuongTon(currentStock - orderedQuantity); // Giảm số lượng
                    sanPhamService.save(product); // LƯU SẢN PHẨM ĐÃ CẬP NHẬT TỒN KHO VÀO DB
                    // =======================================================
                }

                // 4. XÓA GIỎ HÀNG VÀ CHUYỂN HƯỚNG
                SessionCartUtils.clearCart(session);

                redirectAttributes.addFlashAttribute("message", "Đơn hàng đã được đặt thành công!");

                // Chuyển hướng đến trang lịch sử đơn hàng
                return "redirect:/orders";

            } catch (IllegalStateException e) {
                // Xử lý lỗi tồn kho (lỗi này do bạn tự throw)
                redirectAttributes.addFlashAttribute("error", "Đặt hàng thất bại: " + e.getMessage());
                return "redirect:/checkout";
            } catch (Exception e) {
                // Xử lý lỗi hệ thống/DB (e.g. Could not commit JPA transaction)
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("error", "Đặt hàng thất bại do lỗi hệ thống. Vui lòng thử lại.");
                return "redirect:/checkout";
            }
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
