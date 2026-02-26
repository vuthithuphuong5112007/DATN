package com.poly.assaiment_java6.security;

import com.poly.assaiment_java6.repository.NguoiDungRepository;
import com.poly.assaiment_java6.entity.NguoiDung;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID; // Import cho việc tạo tên đăng nhập ngẫu nhiên
// sử lý đăng nhập bằng gg và lưu vào database
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService{
    // tìm use và tạo use
    private final NguoiDungRepository nguoiDungRepository;

    // Sử dụng Constructor Injection
    public CustomOAuth2UserService(NguoiDungRepository nguoiDungRepository) {
        this.nguoiDungRepository = nguoiDungRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Gọi Service mặc định để lấy thông tin user từ nhà cung cấp (Google, Facebook)
        OAuth2User oauth2User = super.loadUser(userRequest);

        // Lấy provider registration ID (ví dụ: "google")
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // Lấy email làm username
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name"); // Tên đầy đủ từ Google

        // 1. Tìm hoặc Tạo người dùng trong DB
        Optional<NguoiDung> existingUser = nguoiDungRepository.findByEmail(email);
        NguoiDung user;
        String role;

        if (existingUser.isPresent()) {
            user = existingUser.get();
            // Cập nhật Họ tên (hoTen) nếu cần
            user.setHoTen(name);

            // Lấy Vai trò (vaiTro) hiện tại
            role = user.getVaiTro();
        } else {
            // Tạo mới người dùng
            user = new NguoiDung();
            user.setEmail(email);
            user.setHoTen(name); // Dùng tên Google làm Họ tên

            // Tạo tên đăng nhập duy nhất từ email (hoặc UUID) vì TenDangNhap là UNIQUE NOT NULL
            String usernameBase = email.substring(0, email.indexOf("@"));
            user.setTenDangNhap(usernameBase + "_" + UUID.randomUUID().toString().substring(0, 4));

            // MatKhau là trường bắt buộc trong Entity
            user.setMatKhau("{noop}OAUTH_USER");

            // Gán Vai trò (vaiTro) mặc định
            user.setVaiTro("USER");

            // *GIẢ ĐỊNH* Entity NguoiDung có trường boolean cho trạng thái kích hoạt (TrangThai)
            // Nếu Entity của bạn không có trường này, hãy xóa hoặc sửa lại dòng này.
            // user.setTrangThai(true);

            // Cần set các trường @NotBlank khác trong Entity (sdt, diaChi)
            // vì Google không cung cấp, nên ta set giá trị mặc định/giả
            user.setSdt("0000000000"); // Số điện thoại giả
            user.setDiaChi("Địa chỉ mặc định (OAuth)"); // Địa chỉ giả

            role = "USER";
        }
        nguoiDungRepository.save(user);

        // 2. Tạo Granted Authority từ Vai trò (vaiTro)
        // Spring Security yêu cầu prefix "ROLE_"
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role);

        // 3. Trả về đối tượng OAuth2User đã được tùy chỉnh
        return new DefaultOAuth2User(
                Collections.singleton(authority),
                oauth2User.getAttributes(),
                // Tên thuộc tính dùng làm Principal. Với Google là "email"
                "name"
        );
    }
}
