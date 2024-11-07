package com.example.manageruser.Controller;


import com.example.manageruser.Model.Notification;
import com.example.manageruser.Model.User;
import com.example.manageruser.Repository.UserRepository;
import com.example.manageruser.Service.NotificationService;
import com.example.manageruser.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class MainController {
    @Autowired
    UserService userService;

    @Autowired
    NotificationService notificationService;

    @GetMapping("")
    public String showHome() {

       return "login";
    }

    @GetMapping("/noti_list")
    public String viewNotificationsPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Get username from Principal
        String username = userDetails.getUsername();

        // Find user by username
        User currentUser = userService.findByUsername(username);

        // Add user ID to the model
        model.addAttribute("userId", currentUser.getId());
        model.addAttribute("usn", username);

        return "notifications";  // this points to notifications.html in the templates directory
    }




}
