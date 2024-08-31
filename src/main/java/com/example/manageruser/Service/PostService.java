package com.example.manageruser.Service;

import com.example.manageruser.Model.Post;
import com.example.manageruser.Repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {



    @Autowired
    private PostRepository postRepository;

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }


    public List<Post> getPostsByFriendShip(String username) {

        List<Post> posts = postRepository.findPostsByFriendship(username);

        return posts;

    }

//    public Page<Post> getPostsByFriendShip(String username, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        return postRepository.findPostsByFriendship(username, pageable);
//    }

}
