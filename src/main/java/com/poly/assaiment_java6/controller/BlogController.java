package com.poly.assaiment_java6.controller;

import com.poly.assaiment_java6.entity.Blog;
import com.poly.assaiment_java6.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/blog")
public class BlogController {
    @Autowired
    private BlogService blogService;

    // LUỒNG 1: HIỆN DANH SÁCH BÀI VIẾT TỪ DATABASE (CRUD)
    @GetMapping("/danh-sach")
    public String listFromDatabase(Model model) {
        model.addAttribute("blogs", blogService.findAll());
        return "blog_vinh/list"; // Trang hiện danh sách từ DB
    }

    @GetMapping("/detail/{id}")
    public String detailFromDatabase(@PathVariable("id") Long id, Model model) {
        Blog blog = blogService.findById(id);
        model.addAttribute("blog", blog);
        return "blog_vinh/detail";
    }

    @GetMapping("/bai-viet/{id}")
    public String blogDetail(@PathVariable String id) {
        return "blog_vinh/blog" + id;
    }




}
