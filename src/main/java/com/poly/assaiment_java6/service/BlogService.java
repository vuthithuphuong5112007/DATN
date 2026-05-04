package com.poly.assaiment_java6.service;

import com.poly.assaiment_java6.entity.Blog;
import java.util.List;
public interface BlogService {
    List<Blog> findAll();

    Blog findById(Long id);

    Blog save(Blog blog);

    void deleteById(Long id);
}
