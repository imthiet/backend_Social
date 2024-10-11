package com.example.manageruser.Controller;

import com.example.manageruser.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class VerificationController {

    @Autowired
    private UserService userService;

    @GetMapping("/verify")
    public String verifyAccount(@RequestParam("code") String verificationCode, Model model) {
        boolean verified = userService.verify(verificationCode);

        if (verified) {
            model.addAttribute("message", "Xác minh tài khoản thành công! Bạn có thể đăng nhập.");
            return "verification_success";
        } else {
            model.addAttribute("message", "Mã xác minh không hợp lệ hoặc tài khoản đã được kích hoạt.");
            return "verification_failure";
        }


    }
}
