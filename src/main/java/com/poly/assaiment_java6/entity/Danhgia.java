package com.poly.assaiment_java6.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "danh_gia") // Tên bảng sẽ được tạo trong Database
public class Danhgia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id")
    private Long productId; // Thêm ID của sản phẩm

    @Column(name = "stars")
    private int stars;

    @Column(name = "content", columnDefinition = "NVARCHAR(255)")
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // --- CONSTRUCTOR RỖNG (Bắt buộc phải có để Spring Boot không báo lỗi 500) ---
    public Danhgia() {
    }

    // --- GETTER VÀ SETTER ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}