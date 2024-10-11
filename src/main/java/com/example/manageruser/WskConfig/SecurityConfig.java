package com.example.manageruser.WskConfig;

import com.example.manageruser.Service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(); // Dịch vụ lấy tài khoản từ cơ sở dữ liệu
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Mã hóa mật khẩu
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // Thiết lập dịch vụ lấy tài khoản
        authProvider.setPasswordEncoder(passwordEncoder); // Thiết lập mã hóa mật khẩu
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/Image/**", "/webjars/**", "/users/**", "/avartar/**").permitAll()
                        .requestMatchers("/users").hasRole("ADMIN")// Cho phép admin dang nhap
                        .anyRequest().authenticated() // Các URL còn lại yêu cầu xác thực
                )
                .formLogin(form -> form
                        .loginPage("/login") // Custom trang login
                        .defaultSuccessUrl("/newsfeed", true)
                        .permitAll() // Cho phép truy cập vào trang login
                )
                .logout(logout -> logout.permitAll()) // Cho phép đăng xuất
                .csrf(csrf -> csrf.disable()); // Tắt bảo vệ CSRF (nếu cần)

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager(); // Quản lý xác thực
    }
}
