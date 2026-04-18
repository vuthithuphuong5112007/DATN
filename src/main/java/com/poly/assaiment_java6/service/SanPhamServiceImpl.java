package com.poly.assaiment_java6.service;

import com.poly.assaiment_java6.dto.ProductDTO;
import com.poly.assaiment_java6.entity.SanPham;
import com.poly.assaiment_java6.repository.SanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SanPhamServiceImpl implements SanPhamService{
    // TIÊM (INJECT) REPOSITORY, KHÔNG PHẢI SERVICE
    @Autowired
    private final SanPhamRepository sanPhamRepository;

    // Constructor Injection
    public SanPhamServiceImpl(SanPhamRepository sanPhamRepository) {
        this.sanPhamRepository = sanPhamRepository;
    }

    // Hàm triển khai: getAllSanPhams()
    @Override
    public List<SanPham> getAllSanPhams() {
        return sanPhamRepository.findAll();
    }

    // Hàm triển khai: saveSanPham()
    @Override
    public SanPham save(SanPham sanPham) {
        return sanPhamRepository.save(sanPham);
    }

    // tim kiem san pham theo ten
    @Override
    public List<SanPham> searchProductsByName(String keyword) {
        return sanPhamRepository.searchProductsByName(keyword);
    }
    // Hàm triển khai: getSanPhamById()
    @Override
    public SanPham getSanPhamById(Integer id) {
        Optional<SanPham> optional = sanPhamRepository.findById(id);
        return optional.orElse(null); // Trả về SanPham hoặc null nếu không tìm thấy
    }

    // Hàm triển khai: deleteSanPhamById()
    @Override
    public void deleteSanPhamById(Integer id) {
        sanPhamRepository.deleteById(id);
    }

    @Override
    public List<ProductDTO> getFeaturedProducts() {
        // Gọi phương thức Custom Query trong Repository
        return sanPhamRepository.findFeaturedProducts();
    }

    @Override
    public List<ProductDTO> getFeaturedProductsSorted() {
        // Trả về List<ProductDTO>
        return sanPhamRepository.findFeaturedProductsSortedByCategory();
    }

    @Override
    public Page<ProductDTO> getFeaturedNuocHoaNu(Pageable pageable) {
        return sanPhamRepository.findFeaturedProductsByCategory("Nước hoa nữ", pageable);
    }

    @Override
    public Page<ProductDTO> getFeaturedNuocHoaNam(Pageable pageable) {
        return sanPhamRepository.findFeaturedProductsByCategory("Nước hoa nam", pageable);
    }

    // Trong ProductServiceImpl.java
    @Override
    public List<ProductDTO> getFeaturedOtherProducts() {
        return sanPhamRepository.findOtherFeaturedProducts();
    }

    @Override
    public Optional<SanPham> findById(Integer id) {
        // JpaRepository.findById(ID) trả về Optional<Entity>
        return sanPhamRepository.findById(id);
    }
}
