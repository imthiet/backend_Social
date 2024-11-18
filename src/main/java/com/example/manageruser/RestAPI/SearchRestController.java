package com.example.manageruser.RestAPI;

import com.example.manageruser.Dto.SearchDTO;
import com.example.manageruser.Dto.UserDto;
import com.example.manageruser.Model.User;
import com.example.manageruser.Service.FriendService;
import com.example.manageruser.Service.SearchService;
import com.example.manageruser.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SearchRestController {

    @Autowired
    SearchService searchService;

    @Autowired
    FriendService friendService;

    @Autowired
    UserService userService;
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(value = "keyword", required = false) String keyword,
                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                    @RequestParam(value = "size", defaultValue = "10") int size,
                                    Principal principal) {
        String currentUsername = principal.getName();
        User currentUser = userService.findByUsername(currentUsername);

        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = searchService.listAll(keyword, pageable, currentUser);

        // Map User to UserDto and check friendship statuses
        List<SearchDTO> userDtos = usersPage.getContent().stream().map(user -> {
            boolean friend = friendService.isFriendAccepted(currentUser, user);
            boolean friendPending = friendService.isFriendPending(currentUser, user);
            boolean friendRequestReceiver = friendService.isCurrentUserFriendRequestReceiver(user, currentUser);

            return new SearchDTO(user.getUsername(), user.getEmail(), friend, friendPending, friendRequestReceiver);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(userDtos);
    }



}
