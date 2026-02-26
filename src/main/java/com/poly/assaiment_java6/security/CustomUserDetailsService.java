package com.poly.assaiment_java6.security;

import com.poly.assaiment_java6.entity.NguoiDung;
import com.poly.assaiment_java6.repository.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService{
    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Override
    public UserDetails loadUserByUsername(String tenDangNhap) throws UsernameNotFoundException {
        // Tìm kiếm người dùng dựa trên tên đăng nhập (tenDangNhap)
        Optional<NguoiDung> nguoiDungOptional = nguoiDungRepository.findByTenDangNhap(tenDangNhap);

        if (!nguoiDungOptional.isPresent()) {
            // Nếu không tìm thấy người dùng, ném ngoại lệ
            throw new UsernameNotFoundException("Tài khoản không tồn tại: " + tenDangNhap);
        }

        NguoiDung nguoiDung = nguoiDungOptional.get();

        // BƯỚC QUAN TRỌNG: LẤY VAI TRÒ VÀ GÁN LÀM AUTHORITIES
        // Vai trò (role) của người dùng phải được gán vào List<GrantedAuthority>
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Vì trong index.html, bạn dùng hasAnyAuthority('ADMIN'), nên ta sẽ gán chính xác tên vai trò:
        String vaiTro = nguoiDung.getVaiTro(); // Lấy giá trị từ cột vai_tro trong DB
        if (vaiTro != null && !vaiTro.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority(vaiTro.toUpperCase()));
        }

        // DEBUG: Bạn nên in ra console để kiểm tra vai trò đã được gán đúng chưa
        System.out.println("DEBUG: User " + tenDangNhap + " loaded with Authority: " + authorities);

        // Trả về đối tượng UserDetails (đối tượng User của Spring Security)
        return new User(
                nguoiDung.getTenDangNhap(), // Tên đăng nhập
                nguoiDung.getMatKhau(),    // Mật khẩu đã được mã hóa BCrypt
                authorities               // Danh sách quyền (Authorities)
        );
    }
}
