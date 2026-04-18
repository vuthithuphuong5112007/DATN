package com.poly.assaiment_java6.controller;


import com.poly.assaiment_java6.dto.ProductDTO;
import com.poly.assaiment_java6.entity.NguoiDung;
import com.poly.assaiment_java6.entity.SanPham;
import com.poly.assaiment_java6.repository.NguoiDungRepository;
import com.poly.assaiment_java6.service.SanPhamService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.transaction.annotation.Transactional; // Import này quan trọng
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

// http://localhost:8080/
@Controller
@Transactional
public class HomeController {
    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;
    private final SanPhamService sanPhamService;

    // Sử dụng Dependency Injection để lấy Repository và PasswordEncoder
    public HomeController(NguoiDungRepository nguoiDungRepository, PasswordEncoder passwordEncoder, SanPhamService sanPhamService ) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.passwordEncoder = passwordEncoder;
        this.sanPhamService = sanPhamService;
    }

    // Xử lý hiển thị trang chủ
    @GetMapping("/")
    public String displayHomePage(
            @RequestParam(defaultValue = "0") int nuPage, // Trang cho nước hoa nữ
            @RequestParam(defaultValue = "0") int namPage, // Trang cho nước hoa nam
            @RequestParam(defaultValue = "4") int size, // Số lượng sản phẩm mỗi trang
            Model model) {

        // Phân trang cho Nước hoa Nữ
        Pageable nuPageable = PageRequest.of(nuPage, size);
        Page<ProductDTO> nuocHoaNuProducts = sanPhamService.getFeaturedNuocHoaNu(nuPageable);
        model.addAttribute("nuocHoaNuProducts", nuocHoaNuProducts);
        model.addAttribute("nuPage", nuPage); // Truyền lại số trang hiện tại để phân trang

        // Phân trang cho Nước hoa Nam
        Pageable namPageable = PageRequest.of(namPage, size);
        Page<ProductDTO> nuocHoaNamProducts = sanPhamService.getFeaturedNuocHoaNam(namPageable);
        model.addAttribute("nuocHoaNamProducts", nuocHoaNamProducts);
        model.addAttribute("namPage", namPage); // Truyền lại số trang hiện tại để phân trang

        List<ProductDTO> otherProducts = sanPhamService.getFeaturedOtherProducts();
        model.addAttribute("otherProducts", otherProducts); // THÊM VÀO MODEL
        return "index";
    }


    // Xử lý hiển thị trang đăng nhập (Được Spring Security gọi đến)
    // Các phương thức /login và /register giữ nguyên...
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    // --- LOGIC ĐĂNG KÝ ---

    // 1. Xử lý GET: Hiển thị form đăng ký
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // Truyền một đối tượng NguoiDung rỗng để form Thymeleaf có thể bind data
        model.addAttribute("nguoiDung", new NguoiDung());
        return "register"; // Trả về templates/register.html
    }

    // 2. Xử lý POST: Lưu người dùng mới vào CSDL
    @Transactional // <--- THÊM ANNOTATION NÀY ĐỂ ĐẢM BẢO VIỆC LƯU VÀO DATABASE ĐƯỢC COMMIT
    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("nguoiDung") NguoiDung nguoiDung, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            // Nếu có lỗi (ví dụ: SĐT không đủ 10 số, họ tên trống,...)
            // Trả về form đăng ký, Spring tự động hiển thị lỗi qua th:errors
            return "register";
        }

        // KIỂM TRA TÊN ĐĂNG NHẬP ĐÃ TỒN TẠI CHƯA
        if (nguoiDungRepository.findByTenDangNhap(nguoiDung.getTenDangNhap()).isPresent()) {
            model.addAttribute("error", "Tên đăng nhập đã được sử dụng. Vui lòng chọn tên khác.");
            return "register"; // Giữ nguyên form đăng ký với thông báo lỗi
        }

        // KIỂM TRA EMAIL ĐÃ TỒN TẠI CHƯA (Optional: Nếu bạn cần ràng buộc email duy nhất)
        if (nguoiDung.getEmail() != null && nguoiDungRepository.findByEmail(nguoiDung.getEmail()).isPresent()) {
            model.addAttribute("error", "Email đã được đăng ký.");
            return "register";
        }

        try {
            // BƯỚC QUAN TRỌNG NHẤT: MÃ HÓA MẬT KHẨU trước khi lưu
            String encodedPassword = passwordEncoder.encode(nguoiDung.getMatKhau());
            nguoiDung.setMatKhau(encodedPassword);

            // Thiết lập vai trò mặc định (đã set trong entity, nhưng thiết lập lại không thừa)
            if (nguoiDung.getVaiTro() == null) {
                nguoiDung.setVaiTro("USER");
            }

            // Lưu người dùng mới vào CSDL
            nguoiDungRepository.save(nguoiDung);

        } catch (Exception e) {
            // Xử lý lỗi CSDL hoặc lỗi khác
            // Log lỗi chi tiết
            System.err.println("Registration error: " + e.getMessage());
            model.addAttribute("error", "Đã xảy ra lỗi trong quá trình đăng ký. Vui lòng thử lại.");
            return "register";
        }

        // Đăng ký thành công, chuyển hướng người dùng đến trang đăng nhập
        return "redirect:/login?registerSuccess=true";
    }
}
