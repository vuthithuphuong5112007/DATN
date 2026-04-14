package com.poly.assaiment_java6.service;

import com.poly.assaiment_java6.dto.MonthlyRevenueDTO;
import com.poly.assaiment_java6.dto.OrderStatusDTO;
import com.poly.assaiment_java6.entity.DonHang;
import com.poly.assaiment_java6.entity.NguoiDung;
import com.poly.assaiment_java6.repository.DonHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class DonHangServiceImpl implements DonHangService{
    @Autowired
    private DonHangRepository donHangRepository;

    @Override
    public DonHang save(DonHang donHang) {
        // Triển khai save()
        return donHangRepository.save(donHang);
    }

    @Override
    public List<DonHang> findByNguoiDung(NguoiDung nguoiDung) {
        return donHangRepository.findByNguoiDungOrderByNgayDatHangDesc(nguoiDung);
    }

    @Override
    public List<DonHang> findAll() {
        // Triển khai findAll()
        return donHangRepository.findAll();
    }

    @Override
    public Optional<DonHang> findById(Integer id) {
        // Triển khai findById() - trả về Optional (hỗ trợ .orElse(null) trong Controller)
        return donHangRepository.findById(id);
    }

    @Override
    public List<MonthlyRevenueDTO> getDailyRevenue(int month, int year) {
        // Gọi phương thức Repository đã viết Query DAY(d.ngayDatHang)
        return donHangRepository.getDailyRevenue(month, year);
    }

    @Override
    public List<OrderStatusDTO> getCountOrdersByStatus() {
        // Gọi phương thức Repository đã viết GROUP BY d.trangThaiDonHang
        return donHangRepository.countOrdersByStatus();
    }
}
