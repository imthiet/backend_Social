package com.example.manageruser.Controller;


import com.example.manageruser.Model.User;
import com.example.manageruser.Model.UserNotFoundException;
import com.example.manageruser.Repository.UserRepository;
import com.example.manageruser.Service.EmailService;
import com.example.manageruser.Service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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


    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    @GetMapping("/users")
    public String showUserList(Model model) {
        List<User> listUser = userService.getAllUsers();
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
    public String saveUser(@ModelAttribute("user") User user, RedirectAttributes ra, HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {

        if(userService.emailExists(user.getEmail()) || userService.usernameExists(user.getUsername())) {
            ra.addFlashAttribute("error", "Email hoặc tên người dùng đã tồn tại");
            return "redirect:/users/new";
        }
        user.setEnabled(false);
        user.setVerificationCode(UUID.randomUUID().toString());
        userService.save(user);

        emailService.sendVerificationEmail(user, getSiteURL(request));
        // Gửi email xác minh


        ra.addFlashAttribute("messages", "Đăng ký thành công! Vui lòng kiểm tra email để xác minh tài khoản.");
        return  "redirect:/users";
    }

    @PostMapping("/users/save_update")
    public String saveUserUpdate(@ModelAttribute("user") User user, RedirectAttributes ra, Model model) {

        User rootUser = userService.findById(user.getId());
        if(!rootUser.getPassword().equals(user.getPassword()))
        {
            ra.addFlashAttribute("error", "Passwords is incorrect!");
            return "redirect:/users/edit/" + user.getId();
        }
        userService.save(user);
        ra.addFlashAttribute("messages", "User Updated Successfull!");
        return  "redirect:/users";
    }



    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id, RedirectAttributes ra) {
        userService.deleteById(id);
        ra.addFlashAttribute("messages", "User Deleted Successfull!");
        return "redirect:/users";
    }

    @GetMapping("/users/edit/{id}")
    public String updateUser(@PathVariable("id") Integer id, Model model,RedirectAttributes ra) {

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

    @GetMapping("/users/login")
    public String showUserLoginForm(Model model) {
        model.addAttribute("user", new User());

        return "Login";

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

    @GetMapping("/verify")
    public String verifyUser(@RequestParam("code") String code, Model model) {
        if (userService.verify(code)) {
            // Nếu xác minh thành công, hiển thị trang xác minh thành công
            return "verification_success"; // Đây là tên của file HTML bạn đã tạo
        } else {
            // Nếu xác minh thất bại, hiển thị trang thông báo thất bại
            return "verification_failure"; // Đây là tên của file HTML bạn đã tạo
        }
    }


}
