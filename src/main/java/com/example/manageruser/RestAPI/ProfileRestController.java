//package com.example.manageruser.RestAPI;
//
//import com.example.manageruser.Dto.UserDto;
//import com.example.manageruser.Model.Post;
//import com.example.manageruser.Model.User;
//import com.example.manageruser.Service.FriendService;
//import com.example.manageruser.Service.PostService;
//import com.example.manageruser.Service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api")
//public class ProfileRestController {
//
//    @Autowired
//    private  UserService userService;
//
//    @Autowired
//    private  FriendService friendService;
//
//    @Autowired
//    private  PostService postService;
//
//
//    @GetMapping("/profile")
//    public ResponseEntity<?> getUserProfile(@RequestParam(defaultValue = "0") int page,
//                                            @RequestParam(defaultValue = "10") int size,
//                                            @RequestHeader("Username") String username) {
//        // Ensure the user is authenticated
//        User user = userService.findByUsername(username);
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found or unauthorized");
//        }
//
//        // Fetch data
//        List<UserDto> friends = friendService.getFriends(username).stream()
//                .map(friend -> new UserDto(friend.getId(), friend.getUsername(), friend.getEmail()))
//                .toList();
//
//        Page<Post> userPostsPage = postService.findPostsByUID(user.getId(), page, size);
//        List<Post> posts = userPostsPage.getContent().stream()
//                .map(post -> new Post(post.getId(), post.getContent(), post.getCreatedAt(), post.getPng()))
//                .toList();
//
//        ProfileResponse response = new ProfileResponse(
//                new UserDTO(user.getId(), user.getUsername(), user.getEmail()),
//                friends,
//                posts,
//                page,
//                userPostsPage.getTotalPages()
//        );
//
//        return ResponseEntity.ok(response);
//    }
//}
