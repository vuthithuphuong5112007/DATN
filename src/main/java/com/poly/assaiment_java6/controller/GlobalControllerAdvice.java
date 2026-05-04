package com.poly.assaiment_java6.controller;

import com.poly.assaiment_java6.entity.GioHang;
import com.poly.assaiment_java6.entity.NguoiDung;
import com.poly.assaiment_java6.repository.GioHangRepository;
import com.poly.assaiment_java6.service.NguoiDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.security.Principal;
import java.util.List;
@ControllerAdvice
public class GlobalControllerAdvice {
    @Autowired private GioHangRepository gioHangRepo;
    @Autowired private NguoiDungService nguoiDungService;

    @ModelAttribute("totalItems")
    public int getTotalItems(Principal principal) {
        if (principal == null) return 0;

        try {
            String tenDangNhap = principal.getName();
            NguoiDung user = nguoiDungService.findByTenDangNhap(tenDangNhap).orElse(null);

            if (user != null) {
                List<GioHang> items = gioHangRepo.findByNguoiDung(user);
                return items.stream().mapToInt(GioHang::getSoLuongMua).sum();
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }
}
