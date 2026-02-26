package com.poly.assaiment_java6.service;

import com.poly.assaiment_java6.entity.DanhMuc;
import com.poly.assaiment_java6.repository.DanhMucRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class DanhMucServiceImpl implements DanhMucService{
    @Autowired
    private DanhMucRepository danhMucRepository;

    @Override
    public List<DanhMuc> findAll() {
        return danhMucRepository.findAll();
    }
    @Override
    public List<DanhMuc> getAll() {
        return danhMucRepository.findAll();
    }
    @Override
    public DanhMuc getById(Integer id) {
        return danhMucRepository.findById(id).orElse(null);
    }

    @Override
    public DanhMuc save(DanhMuc danhMuc) {
        return danhMucRepository.save(danhMuc);
    }

    @Override
    public void deleteById(Integer id) {
        danhMucRepository.deleteById(id);
    }
}
