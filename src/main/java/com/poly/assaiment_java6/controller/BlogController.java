package com.poly.assaiment_java6.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/templates/blog_vinh")
public class BlogController {

    @GetMapping("/{id}")
    public String blogDetail(@PathVariable String id) {
        return "blog_vinh/blog" + id;
    }
}
