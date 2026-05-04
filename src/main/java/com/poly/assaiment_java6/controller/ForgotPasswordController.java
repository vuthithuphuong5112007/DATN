package com.poly.assaiment_java6.controller;

import com.poly.assaiment_java6.service.NguoiDungService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ForgotPasswordController {

    @Autowired
    private NguoiDungService nguoiDungService;

    // BƯỚC 1: NHẬP EMAIL
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, HttpSession session, Model model) {
        try {
            nguoiDungService.sendOtp(email, session);
            model.addAttribute("message", "Mã xác thực đã được gửi đến email của bạn.");
            return "verify-otp";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "forgot-password";
        }
    }

    // BƯỚC 2: XÁC THỰC OTP
    // Thêm GetMapping để tránh lỗi khi người dùng F5 trang nhập mã
    @GetMapping("/verify-otp")
    public String showVerifyOtpForm(HttpSession session) {
        if (session.getAttribute("resetEmail") == null) return "redirect:/forgot-password";
        return "verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") String otp, HttpSession session, Model model) {
        String sessionOtp = (String) session.getAttribute("otp");
        if (sessionOtp != null && sessionOtp.equals(otp)) {
            // Đánh dấu đã xác thực OTP thành công
            session.setAttribute("otpVerified", true);
            return "reset-account";
        }
        model.addAttribute("error", "Mã xác thực không chính xác!");
        return "verify-otp";
    }

    // BƯỚC 3: ĐẶT LẠI TÀI KHOẢN
    // Thêm GetMapping để bảo vệ trang này (phải qua bước OTP mới vào được)
    @GetMapping("/reset-account")
    public String showResetAccountForm(HttpSession session) {
        if (session.getAttribute("otpVerified") == null) {
            return "redirect:/forgot-password";
        }
        return "reset-account";
    }

    @PostMapping("/reset-account")
    public String resetAccount(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               HttpSession session, Model model) {
        try {
            String email = (String) session.getAttribute("resetEmail");

            // Gọi service lưu vào DB
            nguoiDungService.updateAccount(email, username, password);

            // Xóa sạch các dấu vết trong session
            session.removeAttribute("otp");
            session.removeAttribute("resetEmail");
            session.removeAttribute("otpVerified");

            return "redirect:/login?success";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "reset-account";
        }
    }
}