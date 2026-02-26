package com.poly.assaiment_java6.security;

import com.poly.assaiment_java6.entity.NguoiDung;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    private final NguoiDung nguoiDung;

    public CustomUserDetails(NguoiDung nguoiDung) {
        this.nguoiDung = nguoiDung;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Lấy VaiTro từ Entity (ví dụ: "OWNER", "EMPLOYEE", "USER")
        String vaiTro = nguoiDung.getVaiTro();

        // Spring Security yêu cầu tiền tố "ROLE_"
        // Ví dụ: "OWNER" sẽ thành "ROLE_OWNER"
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + vaiTro));
    }

    @Override
    public String getPassword() {
        // Trả về mật khẩu đã được mã hóa từ Entity
        return nguoiDung.getMatKhau();
    }

    @Override
    public String getUsername() {
        // Trả về Tên Đăng nhập từ Entity
        return nguoiDung.getTenDangNhap();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
