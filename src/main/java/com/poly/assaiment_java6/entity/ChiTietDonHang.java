package com.poly.assaiment_java6.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ChiTietDonHang")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietDonHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ChiTiet")
    private Integer idChiTiet;

    @Column(name = "Quantity", nullable = false)
    private Integer quantity;

    @Column(name = "PriceAtOrder", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceAtOrder;

    // Mối quan hệ Many-to-One với DonHang (Bảng con)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_DonHang", nullable = false)
    private DonHang donHang;

    // Mối quan hệ Many-to-One với SanPham (Bảng con)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SanPham", nullable = false)
    private SanPham sanPham;
}
