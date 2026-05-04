package com.poly.assaiment_java6.service;

import com.poly.assaiment_java6.entity.NguoiDung;
import com.poly.assaiment_java6.repository.NguoiDungRepository;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class NguoiDungServiceImpl implements NguoiDungService{
    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    // PHƯƠNG THỨC 1: Cần cho CheckoutController
    @Override
    public Optional<NguoiDung> findByTenDangNhap(String tenDangNhap) {
        // Giả sử NguoiDungRepository có phương thức này
        return nguoiDungRepository.findByTenDangNhap(tenDangNhap);
    }

    @Override
    @Transactional
    public NguoiDung save(NguoiDung nguoiDung) {
        // --- XỬ LÝ MẬT KHẨU ---

        // 1. Nếu là THÊM MỚI (idNguoiDung == null)
        if (nguoiDung.getIdNguoiDung() == null) {
            // Yêu cầu Mật khẩu không trống và Mã hóa
            if (nguoiDung.getMatKhau() == null || nguoiDung.getMatKhau().isEmpty()) {
                throw new RuntimeException("Mật khẩu không được để trống khi thêm mới.");
            }
            String encodedPassword = passwordEncoder.encode(nguoiDung.getMatKhau());
            nguoiDung.setMatKhau(encodedPassword);

        } else {
            // 2. Nếu là CẬP NHẬT (idNguoiDung đã có)

            // 2a. Nếu người dùng nhập mật khẩu mới (trường MatKhau không rỗng)
            if (nguoiDung.getMatKhau() != null && !nguoiDung.getMatKhau().isEmpty()) {
                String encodedPassword = passwordEncoder.encode(nguoiDung.getMatKhau());
                nguoiDung.setMatKhau(encodedPassword);
            } else {
                // 2b. Nếu người dùng KHÔNG nhập mật khẩu mới (để trống): Giữ lại mật khẩu cũ
                NguoiDung existingUser = nguoiDungRepository.findById(nguoiDung.getIdNguoiDung())
                        .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại."));

                // Gán mật khẩu cũ đã mã hóa vào đối tượng mới trước khi lưu
                nguoiDung.setMatKhau(existingUser.getMatKhau());
            }
        }

        // --- 3. Lưu bản ghi ---
        return nguoiDungRepository.save(nguoiDung);
    }

    // PHƯƠNG THỨC 3: Find by ID
    @Override
    public Optional<NguoiDung> findById(Integer id) {
        return nguoiDungRepository.findById(id);
    }

    // PHƯƠNG THỨC 4: Find All
    @Override
    public List<NguoiDung> findAll() {
        return nguoiDungRepository.findAll();
    }

    // PHƯƠNG THỨC 5: Delete by ID
    @Override
    public void deleteById(Integer id) {
        nguoiDungRepository.deleteById(id);
    }


    @Override
    public List<NguoiDung> searchByHoTen(String hoTen) {
        return nguoiDungRepository.findByHoTenContainingIgnoreCase(hoTen);
    }

    // --- LOGIC GỬI OTP XÁC THỰC ---
    @Override
    public void sendOtp(String email, HttpSession session) {
        NguoiDung user = nguoiDungRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại trong hệ thống!"));

        // Tạo mã OTP 6 số ngẫu nhiên
        String otp = String.valueOf((int) ((Math.random() * 900000) + 100000));

        // Lưu OTP và Email vào Session để đối chiếu
        session.setAttribute("otp", otp);
        session.setAttribute("resetEmail", email);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setTo(email);
            helper.setSubject("Mã xác thực OTP - Veloria Perfume");

            String htmlContent = "<div style='font-family: Arial, sans-serif;'>"
                    + "<h3>Chào " + user.getHoTen() + ",</h3>"
                    + "<p>Mã xác thực để thay đổi thông tin tài khoản của bạn là: "
                    + "<b style='color: #8c2a39; font-size: 20px;'>" + otp + "</b></p>"
                    + "<p>Vui lòng không cung cấp mã này cho bất kỳ ai.</p>"
                    + "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi hệ thống khi gửi mail. Vui lòng thử lại sau!");
        }
    }

    // --- LOGIC CẬP NHẬT TÊN ĐĂNG NHẬP VÀ MẬT KHẨU ---
    @Override
    @Transactional
    public void updateAccount(String email, String newUsername, String newPassword) {
        // Log kiểm tra email từ session
        System.out.println("DEBUG: Dang cap nhat cho Email: " + email);

        if (email == null || email.isEmpty()) {
            throw new RuntimeException("Phiên làm việc đã hết hạn, vui lòng thử lại!");
        }

        // Tìm người dùng theo email
        NguoiDung user = nguoiDungRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + email));

        // Cập nhật thông tin mới và mã hóa mật khẩu
        user.setTenDangNhap(newUsername);
        user.setMatKhau(passwordEncoder.encode(newPassword));

        // Lưu trực tiếp xuống Database
        nguoiDungRepository.save(user);

        // In log xác nhận
        System.out.println("DEBUG: Cap nhat thanh cong user: " + newUsername + " voi mat khau moi.");
    }
}
