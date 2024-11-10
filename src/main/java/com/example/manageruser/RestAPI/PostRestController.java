package com.example.manageruser.RestAPI;


import com.example.manageruser.Dto.PostDTO;
import com.example.manageruser.Model.Post;
import com.example.manageruser.Service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostRestController {

    @Autowired
    private PostService postService;

    @GetMapping("/all")
    public ResponseEntity<List<PostDTO>> getAllPost()  {
        return new ResponseEntity<List<PostDTO>>(postService.getAllPosts(), HttpStatus.FOUND);
    }

//    @DeleteMapping("/delete/{id}")
//    public void deletePost(@PathVariable  Long id)
//    {
//        postService.deleteCourse(id);
//    }

    @GetMapping("/{id}")
    public Post getPostByID(@PathVariable Long id)
    {
        return postService.findById(id);
    }

    @PutMapping("/update/{id}")
    public Post updatePost(@RequestBody PostDTO postDTO, @PathVariable Long id) {
        return postService.updatePost(postDTO, id);
    }


}
