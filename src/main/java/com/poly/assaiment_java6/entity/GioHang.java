package com.poly.assaiment_java6.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "GioHang")
@Data // Tự động tạo Getter/Setter để tính tiền
@NoArgsConstructor
@AllArgsConstructor
public class GioHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idGioHang;

    @ManyToOne
    @JoinColumn(name = "IDSanPham")
    private SanPham sanPham;

    @ManyToOne
    @JoinColumn(name = "IDNguoiDung")
    private NguoiDung nguoiDung;

    private int soLuongMua;

}

