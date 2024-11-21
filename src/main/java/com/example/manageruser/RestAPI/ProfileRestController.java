package com.example.manageruser.RestAPI;

import com.example.manageruser.Dto.*;
import com.example.manageruser.Model.Post;
import com.example.manageruser.Model.User;
import com.example.manageruser.Service.*;
import com.example.manageruser.WskConfig.BlobUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/profile")
public class ProfileRestController {

    @Autowired
    private  UserService userService;

    @Autowired
    private  FriendService friendService;

    @Autowired
    private  PostService postService;


    @Autowired
    private  LikeService likeService;

    @Autowired
    private  CommentService commentService;


    @GetMapping("/main")
    public ResponseEntity<UserDto> getUserMainProfile(Principal principal) {
        // Lấy username từ Principal
        String username = principal.getName();

        // Lấy thông tin người dùng từ database
        User user = userService.findByUsername(username);

        // Chuyển đổi thông tin người dùng sang UserDto
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setFriend(false); // Vì đây là profile của chính user, không cần đặt quan hệ friend
        userDto.setFriendPending(false);

        // Chuyển đổi Blob sang Base64 và đặt vào DTO
        if (user.getImage() != null) {
            userDto.setImage(BlobUtil.blobToBase64(user.getImage()));
        } else {
            userDto.setImage(null); // Hoặc một URL hình ảnh mặc định nếu cần
        }

        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }


    @GetMapping("/fr")
    public ResponseEntity<List<FriendDTO>> getUserFr(Principal principal) {
        String username = principal.getName();

        // Lấy danh sách bạn bè
        List<FriendDTO> friends = friendService.getFriendlists(username);

        return new ResponseEntity<>(friends, HttpStatus.OK);
    }
    //get user post by id
    @GetMapping("/post")
    public ResponseEntity<List<PostDTO>> getUserPost(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal) {
        // Lấy thông tin user hiện tại từ Principal
        String username = principal.getName();
        User user = userService.findByUsername(username);
        long userId = user.getId();

        // Gọi service để lấy danh sách PostDTO (dạng List)
        List<PostDTO> postDTOs = postService.getUserPosts(userId, page, size);

        return new ResponseEntity<>(postDTOs, HttpStatus.OK);
    }




}