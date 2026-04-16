package com.poly.assaiment_java6.service;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ActiveUserListener implements HttpSessionListener {
    private final AtomicInteger activeUsers = new AtomicInteger(0);

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        activeUsers.incrementAndGet(); // Có người vào -> tăng 1
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        // Sử dụng logic: nếu lớn hơn 0 thì mới trừ, không thì thôi
        activeUsers.updateAndGet(n -> n > 0 ? n - 1 : 0);

        // Hoặc dùng cách đơn giản hơn:
    /*
    int current = activeUsers.decrementAndGet();
    if (current < 0) {
        activeUsers.set(0); // Nếu lỡ xuống âm thì kéo về 0 ngay
    }
    */
        System.out.println("=== CÓ NGƯỜI RA! Tổng số còn: " + activeUsers.get());
    }

    public int getTotalActiveUsers() {
        return activeUsers.get();
    }

}
