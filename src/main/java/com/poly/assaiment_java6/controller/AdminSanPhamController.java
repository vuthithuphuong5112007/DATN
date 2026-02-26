package com.poly.assaiment_java6.controller;

import com.poly.assaiment_java6.entity.DanhMuc;
import com.poly.assaiment_java6.entity.SanPham;
import com.poly.assaiment_java6.service.DanhMucService;
import com.poly.assaiment_java6.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
@RequestMapping("admin/sanpham") // Đường dẫn quản lý sản phẩm
public class AdminSanPhamController {

    @Autowired
    private final SanPhamService sanPhamService;
    private final DanhMucService danhMucService;


    public AdminSanPhamController(SanPhamService sanPhamService, DanhMucService danhMucService) {
        this.sanPhamService = sanPhamService;
        this.danhMucService = danhMucService;
    }

    // Giả định bạn có DanhMucService để lấy danh sách Danh mục cho dropdown
    // @Autowired
    // private DanhMucService danhMucService;

    // ------------------------------------------------------------------
    // 1. READ (Hiển thị Form + Danh sách sản phẩm)
    // Mapped to /admin/sanpham
    // ------------------------------------------------------------------
    @GetMapping
    public String viewProductManagementPage(Model model) {

        // Đảm bảo Form luôn có một đối tượng SanPham để liên kết (bind)
        // Nếu không có đối tượng "sanPham" (ví dụ: sau khi Redirect), thì tạo mới (Thêm mới)
        if (!model.containsAttribute("sanPham")) {
            SanPham sp = new SanPham();
            sp.setDanhMuc(new DanhMuc()); // Giả sử bạn có Entity DanhMuc
            model.addAttribute("sanPham", sp);
        }

        // Lấy tất cả sản phẩm cho bảng
        model.addAttribute("listSanPhams", sanPhamService.getAllSanPhams());

        // (Bổ sung: Lấy danh sách danh mục cho dropdown trong Form)
        // model.addAttribute("listDMs", danhMucService.getAll());
        model.addAttribute("listDMs", danhMucService.findAll());
        // Trả về template (quan-ly-san-pham.html)
        return "admin/quan-ly-san-pham";
    }

    // ------------------------------------------------------------------
    // 2. CREATE/UPDATE (Lưu sản phẩm) - Xử lý POST từ form
    // Mapped to /admin/sanpham/save
    // ------------------------------------------------------------------
    @PostMapping("/save")
    public String saveSanPham(@ModelAttribute("sanPham") SanPham sanPham, @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              RedirectAttributes redirectAttributes){

        String UPLOAD_DIR = "E:\\Java6_ThayDuy\\Assaiment_Java6\\src\\main\\resources\\static";
        Path uploadPath = Paths.get(UPLOAD_DIR);

        // Logic xử lý upload
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // Lấy tên file gốc
                String originalFileName = imageFile.getOriginalFilename();
                // TẠO TÊN FILE DUY NHẤT (Khuyến khích, tránh trùng lặp)
                String fileName = UUID.randomUUID().toString() + "_" + originalFileName;

                // Định nghĩa đường dẫn lưu trữ
                Path filePath = Paths.get(UPLOAD_DIR, fileName);

                // Lưu file vào thư mục static
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // GÁN TÊN FILE VÀO ENTITY
                sanPham.setDuongDanAnh(fileName);

            } catch (Exception e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("error", "Lỗi khi upload file: " + e.getMessage());
                return "redirect:/admin/sanpham";
            }
        }
        // Logic cho trường hợp Cập nhật không chọn file mới
        // Nếu imageFile rỗng, thuộc tính duongDanAnh sẽ giữ giá trị cũ (từ input hidden)
        // Nếu bạn không dùng UUID, hãy đảm bảo rằng bạn xử lý việc lưu trữ và cập nhật logic cẩn thận hơn.

        try {
            // Gửi đối tượng đến Service để lưu/cập nhật
            sanPhamService.save(sanPham);
            redirectAttributes.addFlashAttribute("message", "Lưu sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lưu sản phẩm vào CSDL thất bại: " + e.getMessage());
        }
        // Chuyển hướng về trang danh sách
        return "redirect:/admin/sanpham";
    }

    // ------------------------------------------------------------------
    // 3. UPDATE (Tải dữ liệu lên Form để Sửa)
    // Mapped to /admin/sanpham/edit/{id}
    // ------------------------------------------------------------------
    @GetMapping("/edit/{id}")
    public String editSanPham(@PathVariable(value = "id") Integer id, RedirectAttributes redirectAttributes) {

        // Lấy Sản phẩm theo ID
        SanPham sanPham = sanPhamService.getSanPhamById(id);

        if (sanPham != null) {
            // Sử dụng addFlashAttribute để gửi đối tượng SanPham qua redirect
            // Đối tượng này sẽ được sử dụng để điền vào Form trong hàm @GetMapping chính
            redirectAttributes.addFlashAttribute("sanPham", sanPham);
        }

        // Chuyển hướng về trang chính
        return "redirect:/admin/sanpham";
    }

    // ------------------------------------------------------------------
    // 4. DELETE (Xóa sản phẩm)
    // Mapped to /admin/sanpham/delete/{id}
    // ------------------------------------------------------------------
    @GetMapping("/delete/{id}")
    public String deleteSanPham(@PathVariable(value = "id") Integer id, RedirectAttributes redirectAttributes) {
        sanPhamService.deleteSanPhamById(id);

        // Thêm thông báo thành công (tùy chọn)
        redirectAttributes.addFlashAttribute("message", "Xóa sản phẩm thành công!");

        // Chuyển hướng về trang danh sách
        return "redirect:/admin/sanpham";
    }
}