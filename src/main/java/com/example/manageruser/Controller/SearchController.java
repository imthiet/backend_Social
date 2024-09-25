package com.example.manageruser.Controller;

import com.example.manageruser.Model.User;
import com.example.manageruser.Repository.UserRepository;
import com.example.manageruser.Service.SearchService;
import com.example.manageruser.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller

public class SearchController {

    @Autowired
    SearchService service;

    @RequestMapping("/search")
    public String index(Model model, @Param("keyword") String keyword){
        List<User> listUser = service.listAll(keyword);

        // Thay đổi tên biến từ studentList thành listStudent để khớp với Thymeleaf
        model.addAttribute("listUser", listUser);
        model.addAttribute("keyword", keyword);

        return "search"; // template search
    }
}
