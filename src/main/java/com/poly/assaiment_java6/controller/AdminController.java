package com.poly.assaiment_java6.controller;


import com.poly.assaiment_java6.entity.SanPham;
import com.poly.assaiment_java6.repository.SanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminController {
    @Autowired
    private SanPhamRepository sanPhamRepo;

    @GetMapping("/admin/home")
    public String adminPage(Model model) {
        List<SanPham> sanPhamList = sanPhamRepo.findAll(); // Lấy tất cả sản phẩm
        model.addAttribute("sanPhamList", sanPhamList);
        return "admin";
    }
}
