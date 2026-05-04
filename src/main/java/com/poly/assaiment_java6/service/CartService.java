package com.poly.assaiment_java6.service;

import com.poly.assaiment_java6.entity.GioHang;
import com.poly.assaiment_java6.entity.NguoiDung;
import com.poly.assaiment_java6.entity.SanPham;
import com.poly.assaiment_java6.repository.GioHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {
    @Autowired
    private GioHangRepository gioHangRepo;

    public void themVaoGio(NguoiDung user, SanPham product, int quantity) {
        // 1. Kiểm tra sản phẩm đã tồn tại trong giỏ chưa
        Optional<GioHang> existingItem = gioHangRepo.findByNguoiDungAndSanPham(user, product);

        if (existingItem.isPresent()) {
            // 2. Nếu có rồi thì cộng thêm số lượng mua
            GioHang item = existingItem.get();
            item.setSoLuongMua(item.getSoLuongMua() + quantity);
            gioHangRepo.save(item);
        } else {
            // 3. Nếu chưa có thì tạo mới bản ghi
            GioHang newItem = new GioHang();
            newItem.setNguoiDung(user);
            newItem.setSanPham(product);
            newItem.setSoLuongMua(quantity);
            gioHangRepo.save(newItem);
        }
    }
}
