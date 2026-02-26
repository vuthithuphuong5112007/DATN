package com.poly.assaiment_java6.service;

import com.poly.assaiment_java6.entity.DonHang;
import com.poly.assaiment_java6.entity.NguoiDung;

import java.util.List;
import java.util.Optional;

public interface DonHangService {
    DonHang save(DonHang donHang);
    List<DonHang> findByNguoiDung(NguoiDung nguoiDung);
    List<DonHang> findAll();
    Optional<DonHang> findById(Integer id);
}
