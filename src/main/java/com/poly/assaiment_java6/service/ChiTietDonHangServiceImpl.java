package com.poly.assaiment_java6.service;

import com.poly.assaiment_java6.dto.Top5SanPhamDTO;
import com.poly.assaiment_java6.entity.ChiTietDonHang;
import com.poly.assaiment_java6.repository.ChiTietDonHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChiTietDonHangServiceImpl implements ChiTietDonHangService{
    @Autowired
    private ChiTietDonHangRepository repo;

    @Override
    public ChiTietDonHang save(ChiTietDonHang chiTietDonHang) {
        return repo.save(chiTietDonHang);
    }
    @Override
    public List<Top5SanPhamDTO> getTop5BestSellers() {
        // Tạo PageRequest để lấy trang đầu tiên (0), kích thước 5 phần tử
        Pageable topFive = PageRequest.of(0, 5);
        return repo.findTopSellingProducts(topFive);
    }
}
