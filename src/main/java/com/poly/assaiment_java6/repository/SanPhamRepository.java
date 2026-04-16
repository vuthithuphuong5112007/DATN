package com.poly.assaiment_java6.repository;

import com.poly.assaiment_java6.dto.ProductDTO;
import com.poly.assaiment_java6.entity.SanPham; // Cập nhật Entity package
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repository cho bảng SanPham
@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Integer>{
    @Query("SELECT NEW com.poly.assaiment_java6.dto.ProductDTO("
            // Sử dụng tên thuộc tính Entity (Ví dụ: idSanPham), KHÔNG phải tên cột DB (id_san_pham)
            + "sp.idSanPham, sp.tenSanPham, sp.giaBan, sp.duongDanAnh, dm.tenDanhMuc) "

            // Sử dụng tên Entity (SanPham), KHÔNG phải tên bảng DB (san_pham)
            + "FROM SanPham sp JOIN sp.danhMuc dm "
            + "WHERE sp.trangThaiHoatDong = True ")
    List<ProductDTO> findFeaturedProducts();
    // tim kiem san pham theo ten
    @Query("SELECT s FROM SanPham s WHERE LOWER(s.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<SanPham> searchProductsByName(@Param("keyword") String keyword);

    // Trong SanPhamRepository.java
    @Query("SELECT NEW com.poly.assaiment_java6.dto.ProductDTO("
            // Các trường SELECT DTO giữ nguyên
            + "sp.idSanPham, sp.tenSanPham, sp.giaBan, sp.duongDanAnh, dm.tenDanhMuc) "

            // FROM và JOIN giữ nguyên
            + "FROM SanPham sp JOIN sp.danhMuc dm "
            + "WHERE sp.trangThaiHoatDong = True "

            // THÊM LOGIC SẮP XẾP VÀO CUỐI
            + "ORDER BY "
            + "CASE "
            + "    WHEN dm.tenDanhMuc = 'Nước hoa nữ' THEN 1 " // Nữ ưu tiên 1
            + "    WHEN dm.tenDanhMuc = 'Nước hoa nam' THEN 2 " // Nam ưu tiên 2
            + "    ELSE 3 "
            + "END, "
            + "sp.tenSanPham ASC") // Sắp xếp phụ theo tên sản phẩm
    List<ProductDTO> findFeaturedProductsSortedByCategory();

    @Query("SELECT NEW com.poly.assaiment_java6.dto.ProductDTO("
            + "sp.idSanPham, sp.tenSanPham, sp.giaBan, sp.duongDanAnh, dm.tenDanhMuc) "
            + "FROM SanPham sp JOIN sp.danhMuc dm "
            + "WHERE sp.trangThaiHoatDong = True AND LOWER(sp.tenSanPham) LIKE LOWER(?1)")
    List<ProductDTO> findProductsByKeyword(String keyword);

    // 1. Phương thức tìm kiếm phân trang
    @Query("SELECT NEW com.poly.assaiment_java6.dto.ProductDTO("
            + "sp.idSanPham, sp.tenSanPham, sp.giaBan, sp.duongDanAnh, dm.tenDanhMuc) "
            + "FROM SanPham sp JOIN sp.danhMuc dm "
            + "WHERE sp.trangThaiHoatDong = True AND LOWER(sp.tenSanPham) LIKE LOWER(?1)")
    Page<ProductDTO> findProductsByKeyword(String keyword, Pageable pageable);

    // 2. Phương thức hiển thị mặc định phân trang
    @Query("SELECT NEW com.poly.assaiment_java6.dto.ProductDTO("
            + "sp.idSanPham, sp.tenSanPham, sp.giaBan, sp.duongDanAnh, dm.tenDanhMuc) "
            + "FROM SanPham sp JOIN sp.danhMuc dm "
            + "WHERE sp.trangThaiHoatDong = True "
            + "ORDER BY dm.tenDanhMuc ASC, sp.tenSanPham ASC") // Có thể giữ logic sắp xếp theo danh mục cũ ở đây
    Page<ProductDTO> findAllActive(Pageable pageable);
    // Nếu bạn chưa có, hãy thêm phương thức này vào Repository
    // Nó sẽ tìm sản phẩm nổi bật theo tên danh mục, có phân trang
    @Query(value = """
    SELECT NEW com.poly.assaiment_java6.dto.ProductDTO(
        sp.idSanPham, sp.tenSanPham, sp.giaBan, sp.duongDanAnh, dm.tenDanhMuc) 
    FROM SanPham sp JOIN sp.danhMuc dm 
    WHERE sp.trangThaiHoatDong = TRUE AND LOWER(dm.tenDanhMuc) = LOWER(:categoryName)
    ORDER BY sp.tenSanPham ASC
    """)
    Page<ProductDTO> findFeaturedProductsByCategory(@Param("categoryName") String categoryName, Pageable pageable);

    // Trong SanPhamRepository.java
    @Query("SELECT NEW com.poly.assaiment_java6.dto.ProductDTO("
            + "sp.idSanPham, sp.tenSanPham, sp.giaBan, sp.duongDanAnh, dm.tenDanhMuc) "
            + "FROM SanPham sp JOIN sp.danhMuc dm " // sp.danhMuc là thuộc tính Entity, không phải ID_DanhMuc
            + "WHERE sp.trangThaiHoatDong = TRUE " // TRUE/FALSE trong JPQL
            + "AND dm.idDanhMuc NOT IN (10, 11) " // NOT IN trong JPQL
            + "ORDER BY dm.idDanhMuc ASC, sp.tenSanPham ASC")
    List<ProductDTO> findOtherFeaturedProducts();

    @Query("SELECT COUNT(sp) FROM SanPham sp WHERE sp.soLuongTon < :threshold")
    long countLowStock(@Param("threshold") int threshold);
}
