package com.poly.assaiment_java6.controller;

import com.poly.assaiment_java6.entity.LienHe;
import com.poly.assaiment_java6.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminContactController {

    @Autowired
    private ContactRepository contactRepository;

    @GetMapping("/admin/contacts")
    public String adminPage(Model model) {
        // 1. Lấy toàn bộ danh sách liên hệ từ Database (sử dụng Entity LienHe)
        List<LienHe> danhSachLienHe = contactRepository.findAll();

        // 2. Đẩy danh sách này sang View với tên biến là "contacts"
        model.addAttribute("contacts", danhSachLienHe);

        // 3. Trả về file giao diện (admin.html)
        return "admin/quan-ly-gop-y";
    }
}