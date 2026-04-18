package com.poly.assaiment_java6.controller;

import com.poly.assaiment_java6.entity.LienHe;
import com.poly.assaiment_java6.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
public class ClientContactController {

    @Autowired
    private ContactRepository contactRepository;

    // 1. Hiển thị trang form liên hệ cho khách
    @GetMapping("/lien-he")
    public String showContactForm(Model model) {
        // Tạo một đối tượng LienHe rỗng để hứng dữ liệu từ form HTML
        model.addAttribute("lienHeObj", new LienHe());
        return "user-contact"; // Tên file HTML chứa cái form của khách hàng
    }

    // 2. Xử lý khi khách hàng bấm nút "Gửi đi"
    @PostMapping("/lien-he/submit")
    public String submitContact(@ModelAttribute("lienHeObj") LienHe lienHe, RedirectAttributes redirectAttributes) {
        // Cập nhật ngày giờ gửi hiện tại
        lienHe.setCreatedAt(LocalDateTime.now());

        // Lưu vào Database
        contactRepository.save(lienHe);

        // Báo thành công và load lại trang
        redirectAttributes.addFlashAttribute("thongBao", "Cảm ơn bạn! Tin nhắn đã được gửi thành công.");

        return "redirect:/lien-he"; // Chuyển hướng lại trang liên hệ để xóa form cũ
    }
}