package com.poly.assaiment_java6.service;

import com.poly.assaiment_java6.entity.ChiTietDonHang;
import com.poly.assaiment_java6.repository.ChiTietDonHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChiTietDonHangServiceImpl implements ChiTietDonHangService{
    @Autowired
    private ChiTietDonHangRepository repo;

    @Override
    public ChiTietDonHang save(ChiTietDonHang chiTietDonHang) {
        return repo.save(chiTietDonHang);
    }
}
