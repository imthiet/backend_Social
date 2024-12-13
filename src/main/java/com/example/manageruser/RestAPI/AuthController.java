package com.example.manageruser.RestAPI;


import com.example.manageruser.Dto.LoginRequest;
import com.example.manageruser.Model.User;
import com.example.manageruser.Repository.UserRepository;
import com.example.manageruser.Service.CustomUserDetailsService;
import com.example.manageruser.Service.EmailService;
import com.example.manageruser.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.RequestEntity.put;

@RestController
public class AuthController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @CrossOrigin(origins = "http://localhost:3000")

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            // Kiểm tra tài khoản có tồn tại trong DB hay không
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Kiểm tra tài khoản đã được kích hoạt hay chưa
            if (!user.isEnabled()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Account not enabled. Please verify your email.");
            }

            // Xác thực username và password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            // Lưu thông tin authentication vào SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Lưu thông tin vào session
            request.getSession().setAttribute("user", user);

            // Trả về thông tin cần thiết cho client
            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("isAdmin", user.isAdmin());
            response.put("enabled", user.isEnabled());
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException ex) {
            // Trường hợp username hoặc password không chính xác
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password.");
        } catch (UsernameNotFoundException ex) {
            // Tài khoản không tồn tại
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid username.");
        } catch (Exception ex) {
            // Các lỗi khác
            ex.printStackTrace(); // Debug lỗi
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }



    @PostMapping("/register")
    public ResponseEntity<?> newUser(@RequestBody User user, HttpServletRequest request) {
        try {
            // Encode the password
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);

            // Set default values
            user.setEnabled(false);
            user.setAdmin(false);
            user.setVerificationCode(UUID.randomUUID().toString());

            // Save the user
            User newUser = userService.addUser(user);

            // Send verification email
            // Inside your try-catch block for sending the email
            try {
                emailService.sendVerificationEmail(newUser, getSiteURL(request));
            } catch (jakarta.mail.MessagingException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to send verification email.");
            }


            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (DataIntegrityViolationException e) {
            // Handle unique constraint violations (e.g., duplicate username or email)
            String errorMessage = "Conflict: ";
            if (e.getMessage().contains("email")) {
                errorMessage += "Email already exists.";
            } else if (e.getMessage().contains("username")) {
                errorMessage += "Username already exists.";
            } else {
                errorMessage += "Username or email already exists.";
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating user.");
        }
    }

    // Helper method to generate the site URL
    private String getSiteURL(HttpServletRequest request) {
        return request.getRequestURL().toString().replace(request.getServletPath(), "");
    }


}
