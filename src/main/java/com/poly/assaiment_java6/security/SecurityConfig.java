package com.poly.assaiment_java6.security;

import com.poly.assaiment_java6.repository.NguoiDungRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;

    // Constructor Injection cho CustomUserDetailsService
    public SecurityConfig(CustomUserDetailsService userDetailsService, CustomOAuth2UserService customOAuth2UserService) {
        this.userDetailsService = userDetailsService;
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

        // Tắt CSRF (Thường cần thiết khi làm việc với REST API/VueJS)
        .csrf(csrf -> csrf.disable())
        // ------------------ CẤU HÌNH PHÂN QUYỀN DỰA TRÊN URL -------------------
        .authorizeHttpRequests(authorize -> authorize
            // Trang công khai
            // FIX: Thêm "/encode-test" vào danh sách permitAll()
            .requestMatchers("/", "/products/**", "/register", "/css/**", "/js/**", "/api/**", "/*.jpg", "/api/products/**").permitAll()
            // Khu vực Quản trị Thống kê (Chỉ cho OWNER)
            .requestMatchers("/admin/statistics/**", "/api/statistics/**").hasAuthority("OWNER")
            // Khu vực Quản trị Chung (Cho OWNER và EMPLOYEE)
            .requestMatchers("/admin/**", "/api/admin/**").hasAnyAuthority("OWNER", "ADMIN")
            // Khu vực Khách hàng đã Đăng nhập (Cho tất cả đã xác thực)
            .requestMatchers("/order/**", "/account/**", "/api/checkout", "/api/account/**").authenticated()
            // FIX: Tất cả request còn lại phải yêu cầu đăng nhập
            .anyRequest().authenticated()
        )


                // ------------------ CẤU HÌNH ĐĂNG NHẬP -------------------
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/authenticate")
                        .defaultSuccessUrl("/", true) // Dùng true để luôn chuyển về trang chủ
                        .failureUrl("/login?error")
                        .permitAll()
                )

                // ------------------ CẤU HÌNH ĐĂNG NHẬP OAUTH2 -------------------
                .oauth2Login(oauth -> oauth
                        // 1. Chỉ định trang đăng nhập tùy chỉnh của bạn
                        .loginPage("/login")
                        // 2. Chuyển hướng khi đăng nhập thành công
                        .defaultSuccessUrl("/", true)
                        // 3. Xử lý khi đăng nhập thất bại
                        .failureUrl("/login?oauth-error")
                        // 4. Cấu hình Service để xử lý thông tin user (Sử dụng Bean đã được inject)
                        .userInfoEndpoint(endpoint -> endpoint
                                .userService(customOAuth2UserService)
                        )
                )

                // ------------------ CẤU HÌNH ĐĂNG XUẤT -------------------
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                // ------------------ XỬ LÝ LỖI TRUY CẬP -------------------
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/access-denied")
                );


        return http.build();
    }


}