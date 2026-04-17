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

@Controller
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    @GetMapping("/contact")
    public String showForm(Model model) {
        // Đưa object vào form để Thymeleaf bind dữ liệu
        model.addAttribute("contactData", new LienHe());
        return "Contact";
    }

    @PostMapping("/contact")
    public String handleContactSubmit(
            @ModelAttribute("contactData") LienHe lienHe,
            RedirectAttributes redirectAttributes) {

        try {
            // Lưu xuống database
            contactRepository.save(lienHe);

            // Thông báo thành công
            redirectAttributes.addFlashAttribute("successMsg",
                    "Cảm ơn " + lienHe.getFullName() + "! Veloria đã nhận được tin nhắn của bạn.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: Không thể gửi tin nhắn lúc này.");
            e.printStackTrace();
        }

        return "redirect:/contact";
    }
}