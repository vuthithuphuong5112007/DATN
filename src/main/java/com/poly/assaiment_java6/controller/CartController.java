package com.poly.assaiment_java6.controller;

import com.poly.assaiment_java6.dto.CartItemDTO;
import com.poly.assaiment_java6.entity.GioHang;
import com.poly.assaiment_java6.entity.NguoiDung;
import com.poly.assaiment_java6.entity.SanPham;
import com.poly.assaiment_java6.model.Cart;
import com.poly.assaiment_java6.repository.GioHangRepository;
import com.poly.assaiment_java6.service.NguoiDungService;
import com.poly.assaiment_java6.service.SanPhamService;
import com.poly.assaiment_java6.utils.SessionCartUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;

@Controller
public class CartController {
    @Autowired private SanPhamService sanPhamService;
    @Autowired private GioHangRepository gioHangRepo;
    @Autowired private NguoiDungService nguoiDungService;

    // --- HÀM TIỆN ÍCH: Lấy người dùng đang đăng nhập ---
    private NguoiDung getCurrentUser(Principal principal) {
        if (principal == null) return null;
        String tenDangNhap = principal.getName();
        return nguoiDungService.findByTenDangNhap(tenDangNhap).orElse(null);
    }

    @PostMapping("/cart/add-ajax/{id}")
    @ResponseBody
    public int addToCartAjax(@PathVariable("id") Integer productId, Principal principal) {
        NguoiDung user = getCurrentUser(principal);
        if (user == null) return 0; // Hoặc trả về mã lỗi để bắt đăng nhập

        SanPham product = sanPhamService.findById(productId).orElse(null);
        if (product != null) {
            // Gọi logic lưu vào Database đã viết ở trên
            Optional<GioHang> existingItem = gioHangRepo.findByNguoiDungAndSanPham(user, product);
            if (existingItem.isPresent()) {
                GioHang item = existingItem.get();
                item.setSoLuongMua(item.getSoLuongMua() + 1);
                gioHangRepo.save(item);
            } else {
                GioHang newItem = new GioHang();
                newItem.setNguoiDung(user);
                newItem.setSanPham(product);
                newItem.setSoLuongMua(1);
                gioHangRepo.save(newItem);
            }
        }

        // Trả về tổng số lượng mới từ Database
        return gioHangRepo.findByNguoiDung(user).stream()
                .mapToInt(GioHang::getSoLuongMua).sum();
    }

    // 2. XEM GIỎ HÀNG (LẤY DỮ LIỆU TỪ DATABASE)
    @GetMapping("/cart")
    public String viewCart(Model model, Principal principal) {
        NguoiDung user = getCurrentUser(principal);
        if (user == null) return "redirect:/login";

        // Lấy tất cả sản phẩm trong giỏ của User từ DB
        List<GioHang> cartItems = gioHangRepo.findByNguoiDung(user);

        // Tính tổng tiền thủ công
        BigDecimal totalPrice = cartItems.stream()
                .map(item -> item.getSanPham().getGiaBan().multiply(new BigDecimal(item.getSoLuongMua())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        return "cart";
    }

    // 3. XÓA SẢN PHẨM KHỎI GIỎ HÀNG (XÓA TRONG DATABASE)
    @GetMapping("/cart/remove/{id}")
    public String removeCartItem(@PathVariable("id") Integer idGioHang, RedirectAttributes redirectAttributes) {
        // Lưu ý: id ở đây là ID của bản ghi Giỏ hàng (idGioHang)
        gioHangRepo.deleteById(idGioHang);
        redirectAttributes.addFlashAttribute("message", "Đã xóa sản phẩm khỏi giỏ hàng.");
        return "redirect:/cart";
    }

    // 5. HIỂN THỊ TRANG THANH TOÁN (CHECKOUT)
    @GetMapping("/checkout")
    public String showCheckoutPage(
            @RequestParam(value = "productIds", required = false) List<Integer> productIds,
            @RequestParam(value = "quantities", required = false) List<Integer> quantities,
            Model model, Principal principal) {

        NguoiDung user = getCurrentUser(principal);
        if (user == null) return "redirect:/login";

        // Nếu không chọn gì cả thì bắt quay về giỏ hàng
        if (productIds == null || productIds.isEmpty()) {
            return "redirect:/cart";
        }

        List<GioHang> itemsToCheckout = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        // Duyệt qua danh sách ID được gửi lên từ trang Giỏ hàng hoặc trang Chi tiết
        for (int i = 0; i < productIds.size(); i++) {
            Integer pId = productIds.get(i);
            Integer qty = (quantities != null && i < quantities.size()) ? quantities.get(i) : 1;

            SanPham sp = sanPhamService.findById(pId).orElse(null);
            if (sp != null) {
                GioHang item = new GioHang();
                item.setSanPham(sp);
                item.setSoLuongMua(qty);
                itemsToCheckout.add(item);

                totalPrice = totalPrice.add(sp.getGiaBan().multiply(new BigDecimal(qty)));
            }
        }

        model.addAttribute("cartItems", itemsToCheckout);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("nguoiDung", user);

        return "checkout";
    }

    @PostMapping("/cart/update-quantity/{id}")
    @ResponseBody
    public String updateQuantity(@PathVariable("id") Integer idGioHang, @RequestParam("quantity") int quantity) {
        Optional<GioHang> itemOpt = gioHangRepo.findById(idGioHang);
        if (itemOpt.isPresent()) {
            GioHang item = itemOpt.get();
            item.setSoLuongMua(quantity);
            gioHangRepo.save(item); // Lưu số lượng mới vào DB
            return "Success";
        }
        return "Error";
    }

    @PostMapping("/cart/add")
    @ResponseBody
    public ResponseEntity<?> addToCart(@RequestParam("id") Integer productId,
                                       @RequestParam("qty") Integer quantity,
                                       Principal principal) {
        // 1. Kiểm tra đăng nhập
        NguoiDung user = getCurrentUser(principal);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Chưa đăng nhập");
        }

        try {
            // 2. Tìm sản phẩm
            SanPham product = sanPhamService.findById(productId).orElse(null);
            if (product == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sản phẩm không tồn tại");
            }

            // 3. Logic lưu Database (Giống hàm add-ajax của bạn)
            Optional<GioHang> existingItem = gioHangRepo.findByNguoiDungAndSanPham(user, product);
            if (existingItem.isPresent()) {
                GioHang item = existingItem.get();
                item.setSoLuongMua(item.getSoLuongMua() + quantity); // Cộng dồn số lượng khách chọn
                gioHangRepo.save(item);
            } else {
                GioHang newItem = new GioHang();
                newItem.setNguoiDung(user);
                newItem.setSanPham(product);
                newItem.setSoLuongMua(quantity);
                gioHangRepo.save(newItem);
            }

            // 4. Tính tổng số lượng món đồ trong giỏ để trả về cho Header
            int totalItems = gioHangRepo.findByNguoiDung(user).stream()
                    .mapToInt(GioHang::getSoLuongMua).sum();

            Map<String, Object> response = new HashMap<>();
            response.put("totalItems", totalItems);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi lưu vào Database");
        }
    }
}
