package com.poly.assaiment_java6.service;

import com.poly.assaiment_java6.dto.ProductDTO;
import com.poly.assaiment_java6.entity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SanPhamService {
    List<SanPham> getAllSanPhams();

    // 2. Thêm hàm saveSanPham()
    SanPham save(SanPham sanPham);

    // 3. Thêm hàm getSanPhamById()
    SanPham getSanPhamById(Integer id);

    // 4. Thêm hàm deleteSanPhamById()
    void deleteSanPhamById(Integer id);

    List<ProductDTO> getFeaturedProducts();

    List<ProductDTO> getFeaturedProductsSorted();

    Page<ProductDTO> getFeaturedNuocHoaNu(Pageable pageable);
    Page<ProductDTO> getFeaturedNuocHoaNam(Pageable pageable);

    // Trong IProductService.java
    List<ProductDTO> getFeaturedOtherProducts();

    Optional<SanPham> findById(Integer id);


}
