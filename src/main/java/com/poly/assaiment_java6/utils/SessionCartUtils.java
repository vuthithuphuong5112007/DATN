package com.poly.assaiment_java6.utils;

import com.poly.assaiment_java6.dto.CartItemDTO;
import com.poly.assaiment_java6.entity.SanPham;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SessionCartUtils {
    private static final String MAIN_CART_SESSION_KEY = "shoppingCart";
    private static final String TEMP_CART_SESSION_KEY = "tempCheckoutItems";

    public static void saveItems(HttpSession session, List<CartItemDTO> items) {
        session.setAttribute(TEMP_CART_SESSION_KEY, items);
    }

    public static List<CartItemDTO> getItems(HttpSession session) {
        // Thử lấy túi 1
        List<CartItemDTO> items = (List<CartItemDTO>) session.getAttribute(TEMP_CART_SESSION_KEY);
        // Nếu túi 1 rỗng thì lấy túi 2
        if (items == null || items.isEmpty()) {
            items = (List<CartItemDTO>) session.getAttribute(MAIN_CART_SESSION_KEY);
        }
        return items;
    }

    public static BigDecimal calculateTotal(List<CartItemDTO> items) {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItemDTO item : items) {
            totalPrice = totalPrice.add(item.getSubtotal());
        }
        return totalPrice;
    }

    public static void clearCart(HttpSession session) {
        session.removeAttribute(TEMP_CART_SESSION_KEY);
        session.removeAttribute(MAIN_CART_SESSION_KEY);
    }

    public static List<CartItemDTO> getCartItems(HttpSession session) {
        // Trả về danh sách hiện tại hoặc List rỗng (ArrayList mới) nếu chưa có
        List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute(MAIN_CART_SESSION_KEY);
        return cart != null ? cart : new ArrayList<>();
    }
    /**
     * 2. Thêm hoặc cập nhật sản phẩm vào giỏ hàng chính.
     */
    public static void addItem(HttpSession session, SanPham product, int quantity) {
        List<CartItemDTO> cartItems = getCartItems(session);

        Optional<CartItemDTO> existingItemOpt = cartItems.stream()
                .filter(item -> item.getProduct().getIdSanPham().equals(product.getIdSanPham()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItemDTO existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setPrice(product.getGiaBan());
        } else {
            CartItemDTO newItem = new CartItemDTO();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setPrice(product.getGiaBan());
            cartItems.add(newItem);
        }

        // Lưu List đã cập nhật trở lại Session
        session.setAttribute(MAIN_CART_SESSION_KEY, cartItems);
    }

    // 3. Phương thức để xóa giỏ hàng chính (dùng khi hoàn tất đặt hàng)
    public static void clearMainCart(HttpSession session) {
        session.removeAttribute(MAIN_CART_SESSION_KEY);
    }

    public static boolean removeItem(HttpSession session, Integer productId) {
        List<CartItemDTO> cartItems = getCartItems(session);

        // Tìm và xóa mục hàng
        boolean removed = cartItems.removeIf(item -> item.getProduct().getIdSanPham().equals(productId));

        if (removed) {
            // Lưu danh sách đã cập nhật trở lại Session
            session.setAttribute(MAIN_CART_SESSION_KEY, cartItems);
        }
        return removed;
    }
}
