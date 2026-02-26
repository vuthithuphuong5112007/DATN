package com.poly.assaiment_java6.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "SanPham")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_SanPham")
    private Integer idSanPham;

    @Column(name = "TenSanPham", nullable = false)
    private String tenSanPham;

    @Column(name = "MoTa", columnDefinition = "NVARCHAR(MAX)") // Dùng NVARCHAR(MAX) trong SQL Server
    private String moTa;

    @Column(name = "GiaBan", nullable = false, precision = 10, scale = 2)
    private BigDecimal giaBan; // Dùng BigDecimal cho tiền tệ để tránh sai sót

    @Column(name = "SoLuongTon", nullable = false)
    private Integer soLuongTon;

    @Column(name = "DuongDanAnh")
    private String duongDanAnh;

    @Column(name = "TrangThaiHoatDong")
    private Boolean trangThaiHoatDong; // Bit trong SQL Server được ánh xạ thành Boolean

    // Mối quan hệ Many-to-One với DanhMuc (Bảng con)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_DanhMuc", nullable = false) // Khóa ngoại ID_DanhMuc
    private DanhMuc danhMuc;

    // Mối quan hệ One-to-Many với ChiTietDonHang
    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChiTietDonHang> chiTietDonHangs;
}
