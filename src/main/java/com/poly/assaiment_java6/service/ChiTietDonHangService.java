package com.poly.assaiment_java6.service;

import com.poly.assaiment_java6.dto.Top5SanPhamDTO;
import com.poly.assaiment_java6.entity.ChiTietDonHang;

import java.util.List;

public interface ChiTietDonHangService {
    ChiTietDonHang save(ChiTietDonHang chiTietDonHang);
    List<Top5SanPhamDTO> getTop5BestSellers();
}
