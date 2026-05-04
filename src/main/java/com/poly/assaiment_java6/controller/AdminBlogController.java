package com.poly.assaiment_java6.controller;

import com.poly.assaiment_java6.entity.Blog;
import com.poly.assaiment_java6.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Controller
@RequestMapping("/admin/blog")
public class AdminBlogController {
    @Autowired
    private BlogService blogService;

    private final String UPLOAD_DIR = "src/main/resources/static/uploads/blog/";

    @GetMapping
    public String blogPage(Model model) {
        model.addAttribute("blog", new Blog());
        model.addAttribute("blogs", blogService.findAll());
        return "admin/blog";
    }

    @PostMapping("/save")
    public String save(
            @ModelAttribute Blog blog,
            @RequestParam("imageFile") MultipartFile imageFile
    ) throws IOException {

        // upload ảnh
        if (!imageFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();

            Path path = Paths.get(UPLOAD_DIR);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            Files.write(Paths.get(UPLOAD_DIR + fileName), imageFile.getBytes());
            blog.setImage(fileName);
        }

        blogService.save(blog);
        return "redirect:/admin/blog";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("blog", blogService.findById(id));
        model.addAttribute("blogs", blogService.findAll());
        return "admin/blog";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        blogService.deleteById(id);
        return "redirect:/admin/blog";
    }
}
