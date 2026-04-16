package com.poly.assaiment_java6.testpostman;

import com.poly.assaiment_java6.entity.DanhMuc;
import com.poly.assaiment_java6.service.DanhMucService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // Dùng cái này Postman mới hiện JSON
@RequestMapping("/api/admin/danhmuc")
public class AdminDanhMucRestController {
    @Autowired
    private DanhMucService danhMucService;

    @GetMapping
    public List<DanhMuc> getAll() {
        return danhMucService.findAll(); // Trả về danh sách dạng JSON
    }
}
