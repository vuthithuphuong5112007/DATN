package com.poly.assaiment_java6.repository;

import com.poly.assaiment_java6.entity.Danhgia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Danhgia, Long> {

    // Đã đổi: Tìm đánh giá dựa theo product_id và sắp xếp mới nhất
    List<Danhgia> findByProductIdOrderByCreatedAtDesc(Long productId);
}