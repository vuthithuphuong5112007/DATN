package com.poly.assaiment_java6.service;

import com.poly.assaiment_java6.entity.NguoiDung;
import com.poly.assaiment_java6.repository.NguoiDungRepository;
import com.poly.assaiment_java6.service.NguoiDungService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder; // Thường được dùng
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class NguoiDungServiceImpl implements NguoiDungService{
    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Đảm bảo phải có

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
}
