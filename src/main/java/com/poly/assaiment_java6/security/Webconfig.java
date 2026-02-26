package com.poly.assaiment_java6.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean; // Thêm import này
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
public class Webconfig {
    @Bean
    public FilterRegistrationBean<CharacterEncodingFilter> encodingFilterRegistration() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true); // Bắt buộc mã hóa

        FilterRegistrationBean<CharacterEncodingFilter> registration = new FilterRegistrationBean<>(filter);

        // --- THIẾT LẬP ƯU TIÊN CAO NHẤT ---
        // Đảm bảo Filter này chạy trước Spring Security và mọi Filter khác
        registration.setOrder(Integer.MIN_VALUE);
        registration.addUrlPatterns("/*"); // Áp dụng cho mọi URL

        return registration;
    }
}
