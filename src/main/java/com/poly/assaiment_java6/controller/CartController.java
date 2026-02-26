package com.poly.assaiment_java6.controller;

import com.poly.assaiment_java6.dto.CartItemDTO;
import com.poly.assaiment_java6.entity.SanPham;
import com.poly.assaiment_java6.model.Cart;
import com.poly.assaiment_java6.service.SanPhamService;
import com.poly.assaiment_java6.utils.SessionCartUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class CartController {
    @Autowired
    private SanPhamService sanPhamService;


    @PostMapping("/cart/add") // SỬ DỤNG @PostMapping vì đây là hành động thay đổi dữ liệu (thêm)
    public String addToCart(
            @RequestParam("id") Integer productId, // Nhận ID sản phẩm từ form
            @RequestParam(value = "quantity", defaultValue = "1") Integer quantity, // Nhận số lượng
            HttpSession session,
            RedirectAttributes redirectAttributes)
    {
        try {
            // 1. TÌM SẢN PHẨM TRONG DB
            SanPham product = sanPhamService.findById(productId).orElseThrow(() -> new Exception("Sản phẩm không tồn tại"));

            // 2. THÊM VÀO GIỎ HÀNG (SỬ DỤNG UTILS HOẶC SERVICE)
            SessionCartUtils.addItem(session, product, quantity);

            redirectAttributes.addFlashAttribute("message", "Đã thêm sản phẩm vào giỏ hàng!");

            // 3. CHUYỂN HƯỚNG VỀ TRANG GIỎ HÀNG
            return "redirect:/cart";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            // Chuyển hướng lại về trang chi tiết sản phẩm (hoặc trang shop)
            return "redirect:/products/detail/" + productId;
        }
    }

    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {

        // 1. Lấy danh sách sản phẩm trong giỏ hàng từ session
        List<CartItemDTO> cartItems = SessionCartUtils.getCartItems(session);

        // 2. Tính tổng tiền (nếu cần)
        BigDecimal totalPrice = SessionCartUtils.calculateTotal(cartItems);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        return "cart";
    }

    // Trong CartController.java (hoặc nơi bạn quản lý logic Giỏ hàng)

    @GetMapping("/cart/remove/{id}")
    public String removeCartItem(@PathVariable("id") Integer productId, HttpSession session, RedirectAttributes redirectAttributes) {

        // 1. Lấy giỏ hàng chính từ Session
        List<CartItemDTO> cartItems = SessionCartUtils.getCartItems(session);

        if (cartItems != null) {
            // 2. Xóa sản phẩm khỏi danh sách
            boolean removed = cartItems.removeIf(item -> item.getProduct().getIdSanPham().equals(productId));

            if (removed) {
                // 3. Lưu lại danh sách đã cập nhật vào Session
                session.setAttribute("shoppingCart", cartItems); // Dùng MAIN_CART_SESSION_KEY là "shoppingCart"
                redirectAttributes.addFlashAttribute("message", "Đã xóa sản phẩm khỏi giỏ hàng.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm để xóa.");
            }
        }

        // 4. Chuyển hướng trở lại trang Giỏ hàng
        return "redirect:/cart";
    }

    @ModelAttribute("totalItems")
    public int getTotalItems(HttpSession session) {
        // Giả sử bạn lưu giỏ hàng trong Session với tên "cart"
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart != null) {
            // Trả về tổng số lượng (quantity) của tất cả sản phẩm trong giỏ
            return cart.getCartItems().stream().mapToInt(item -> item.getQuantity()).sum();
        }
        return 0;
    }

    @PostMapping("/cart/add-ajax/{id}")
    @ResponseBody
    public int addToCartAjax(@PathVariable("id") Integer id, HttpSession session) {
        // Sử dụng Service và Utils bạn đã có sẵn trong dự án
        SanPham product = sanPhamService.findById(id).orElse(null);
        if (product != null) {
            SessionCartUtils.addItem(session, product, 1);
        }
        // Trả về tổng số lượng từ List<CartItemDTO>
        List<CartItemDTO> cartItems = SessionCartUtils.getCartItems(session);
        return cartItems.stream().mapToInt(item -> item.getQuantity()).sum();
    }
}
