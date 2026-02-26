package com.poly.assaiment_java6.service;

import com.poly.assaiment_java6.entity.DanhMuc;

import java.util.List;

public interface DanhMucService {
    List<DanhMuc> findAll();
    List<DanhMuc> getAll(); // Lấy tất cả danh mục
    DanhMuc getById(Integer id); // Lấy danh mục theo ID
    DanhMuc save(DanhMuc danhMuc); // Thêm mới hoặc Cập nhật
    void deleteById(Integer id); // Xóa danh mục
}
