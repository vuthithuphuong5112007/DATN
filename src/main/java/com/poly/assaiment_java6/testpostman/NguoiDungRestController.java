package com.poly.assaiment_java6.testpostman;
import com.poly.assaiment_java6.entity.NguoiDung; // Hoặc Account/User tùy bạn đặt
import com.poly.assaiment_java6.service.NguoiDungService; // Hoặc AccountService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class NguoiDungRestController {
    @Autowired
    private NguoiDungService nguoiDungService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<NguoiDung> getAllAccounts() {
        // Trả về danh sách tất cả người dùng (Admin, Nhân viên, Khách hàng)
        return nguoiDungService.findAll();
    }

    @PostMapping
    public NguoiDung createAccount(@RequestBody NguoiDung nguoiDung) {
        // Sửa dòng bị lỗi đỏ: gán mật khẩu đã mã hóa vào đối tượng
        String encodedPass = passwordEncoder.encode(nguoiDung.getMatKhau());
        nguoiDung.setMatKhau(encodedPass);

        return nguoiDungService.save(nguoiDung);
    }

    @PutMapping("/{id}") // Nhận ID từ URL
    public NguoiDung updateAccount(@PathVariable("id") Integer id, @RequestBody NguoiDung nguoiDungData) {
        // Tìm người dùng cũ trong DB bằng Optional
        NguoiDung existingUser = nguoiDungService.findById(id).orElse(null);

        if (existingUser != null) {
            // Cập nhật các thông tin mới
            existingUser.setHoTen(nguoiDungData.getHoTen());
            existingUser.setDiaChi(nguoiDungData.getDiaChi());
            existingUser.setSdt(nguoiDungData.getSdt());
            existingUser.setMatKhau(nguoiDungData.getMatKhau());
            // ... bạn có thể set thêm các trường khác nếu cần

            return nguoiDungService.save(existingUser); // Lưu đè lên bản ghi cũ
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public String deleteAccount(@PathVariable("id") Integer id) {
        try {
            // 1. Kiểm tra xem người dùng có tồn tại trong SQL Server không
            if (nguoiDungService.findById(id).isPresent()) {

                // 2. Gọi service để thực hiện lệnh DELETE trong SQL
                nguoiDungService.deleteById(id);

                return "Xóa thành công người dùng có ID: " + id;
            } else {
                return "Không tìm thấy người dùng với ID: " + id;
            }
        } catch (Exception e) {
            // Trường hợp người dùng này đã có hóa đơn, SQL sẽ chặn không cho xóa
            return "Lỗi: Không thể xóa vì người dùng này đã có dữ liệu liên quan (hóa đơn, giỏ hàng...)";
        }
    }


}
