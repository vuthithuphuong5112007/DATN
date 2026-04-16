package com.poly.assaiment_java6.repository;

import com.poly.assaiment_java6.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Blog, Long> {
}
