package com.poly.assaiment_java6.controller;

import com.poly.assaiment_java6.service.ActiveUserListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // Dùng RestController để trả về dữ liệu thuần
public class ApiAdminController {
    @Autowired
    private ActiveUserListener activeUserListener;

    @GetMapping("/api/admin/online-count")
    public int getOnlineCount() {
        return activeUserListener.getTotalActiveUsers();
    }
}
