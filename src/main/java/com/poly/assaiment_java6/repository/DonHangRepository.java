package com.poly.assaiment_java6.repository;

import com.poly.assaiment_java6.entity.DonHang;
import com.poly.assaiment_java6.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonHangRepository extends JpaRepository<DonHang, Integer>{
    List<DonHang> findByNguoiDungOrderByNgayDatHangDesc(NguoiDung nguoiDung);
}
