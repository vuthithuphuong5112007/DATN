package com.poly.assaiment_java6.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "DonHang")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DonHang")
    private Integer idDonHang;

    @Column(name = "NgayDatHang")
    @Temporal(TemporalType.TIMESTAMP) // Xử lý kiểu DATETIME của SQL Server
    private Date ngayDatHang;

    @Column(name = "TongTien", nullable = false, precision = 10, scale = 2)
    private BigDecimal tongTien;

    @Column(name = "DiaChiGiaoHang")
    private String diaChiGiaoHang;

    @Column(name = "TrangThaiDonHang", nullable = false)
    private String trangThaiDonHang;

    // Mối quan hệ Many-to-One với NguoiDung (Bảng con)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_NguoiDung")
    private NguoiDung nguoiDung;

    // Mối quan hệ One-to-Many với ChiTietDonHang (Bảng cha)
    @OneToMany(mappedBy = "donHang", fetch = FetchType.EAGER)
    private List<ChiTietDonHang> chiTietDonHang;
}
