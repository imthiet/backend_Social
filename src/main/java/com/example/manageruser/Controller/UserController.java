package com.example.manageruser.Controller;


import com.example.manageruser.Model.User;
import com.example.manageruser.Dto.UserDto;
import com.example.manageruser.Model.UserNotFoundException;
import com.example.manageruser.Repository.UserRepository;
import com.example.manageruser.Service.EmailService;
import com.example.manageruser.Service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.MessagingException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;


@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    @GetMapping("/users")
    public String showUserList(RedirectAttributes ra, Model model) {
        // Lấy thông tin người dùng từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : null;

        if (username == null) {
            ra.addFlashAttribute("error", "Vui lòng đăng nhập để truy cập trang này.");
            return "redirect:/login";
        }

        // Lấy thông tin người dùng từ cơ sở dữ liệu theo tên đăng nhập
        User currentUser = userService.findByUsername(username);

        if (currentUser == null || !currentUser.isAdmin()) {
            ra.addFlashAttribute("error", "Bạn không có quyền truy cập trang này.");
            return "redirect:/newsfeed";
        }

        // Nếu người dùng là admin, hiển thị danh sách người dùng
        List<UserDto> listUser = userService.getAllUsers();
        model.addAttribute("listUser", listUser);

        return "users";
    }


    @GetMapping("/users/new")
    public String showUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("pageTitle", "Add New User");
        return "user_form";

    }

    @PostMapping("/users/save")
    public String saveUser(
            @Valid @ModelAttribute("user") User user,
            BindingResult bindingResult,
            RedirectAttributes ra,
            HttpServletRequest request) throws UnsupportedEncodingException {

        if (bindingResult.hasErrors()) {
            return "users/new";
        }

        try {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            user.setEnabled(false);
            user.setAdmin(false);
            user.setVerificationCode(UUID.randomUUID().toString());

            userService.save(user);
        } catch (DataIntegrityViolationException e) {
            // Kiểm tra lỗi vi phạm unique key và thêm thông báo lỗi
            ra.addFlashAttribute("error", "Username hoặc email đã tồn tại.");
            return "redirect:/users/new";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Existed Email Or Username!");
            return "redirect:/users/new";
        }

        try {
            emailService.sendVerificationEmail(user, getSiteURL(request));
        } catch (MessagingException e) {
            ra.addFlashAttribute("error", "Gửi email xác minh thất bại. Vui lòng thử lại.");
            return "redirect:/users/new";
        } catch (jakarta.mail.MessagingException e) {
            throw new RuntimeException(e);
        }

        ra.addFlashAttribute("error", "Sign in successful! Please check your email to verification!.");
        return "redirect:/login";
    }


    @PostMapping("/users/save_update")
    public String saveUserUpdate(@ModelAttribute("user") User user, RedirectAttributes ra, Model model) {

        User rootUser = userService.findById(user.getId());
        if (!rootUser.getPassword().equals(user.getPassword())) {
            ra.addFlashAttribute("error", "Passwords is incorrect!");
            return "redirect:/users/edit/" + user.getId();
        }
        userService.save(user);
        ra.addFlashAttribute("messages", "User Updated Successfull!");
        return "redirect:/users";
    }

    @PostMapping("/users/save_edit")
    public String saveUserEdit(@ModelAttribute("user") User user, RedirectAttributes ra) {

        // Tìm User trong cơ sở dữ liệu theo ID
        User rootUser = userService.findById(user.getId());

        if (rootUser != null) {
            // Cập nhật trạng thái 'enabled' của User
            rootUser.setEnabled(user.isEnabled());
            rootUser.setAdmin(user.isAdmin());
            // Lưu user đã cập nhật vào cơ sở dữ liệu
            userService.save(rootUser);

            ra.addFlashAttribute("messages", "User Updated Successfully!");
        } else {
            ra.addFlashAttribute("error", "User not found!");
        }

        return "redirect:/users";
    }


    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id, RedirectAttributes ra) {
        userService.deleteById(id);
        ra.addFlashAttribute("messages", "User Deleted Successfull!");
        return "redirect:/users";
    }

    @GetMapping("/users/edit/{id}")
    public String updateUser(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {

        try {
            User user = userService.get(id);
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Update User " + id);

            return "user_update_form";
        } catch (UserNotFoundException e) {
            ra.addFlashAttribute("messages", "User updated!");
            return "redirect:/users";

        }

    }

    @GetMapping("/login")
    public String showUserLoginForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password. Please try again.");
        }
        model.addAttribute("user", new User());
        return "login"; // template login
    }


    @PostMapping("/users/validate")
    public String login(@ModelAttribute("user") User user, RedirectAttributes ra, Model model, HttpSession session) {

        User rootUser = userService.findByEmail(user.getEmail());

        if (rootUser == null || !rootUser.getPassword().equals(user.getPassword())) {
            ra.addFlashAttribute("error", "Email or Password is incorrect!");
            return "redirect:/users/login";
        }

        // Xử lý lưu trữ session hoặc các thông tin đăng nhập thành công ở đây

        ra.addFlashAttribute("messages", "Login Successful!");


        // Lưu trữ username vào session
        session.setAttribute("username", rootUser.getUsername());
        return "redirect:/newsfeed";

        // Chuyển hướng về trang chính sau khi đăng nhập thành công
    }


    // Xử lý yêu cầu POST để thực hiện logout
    @GetMapping("/users/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // Hủy session hiện tại
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Xóa tất cả cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookie.setValue(null);
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }

        // Chuyển hướng đến trang đăng nhập
        return "redirect:/users/login";
    }


    @GetMapping("/test")
    public String sc(Model model) {

        return "verification_success"; // Đây là tên của file HTML bạn đã tạo

    }


}
