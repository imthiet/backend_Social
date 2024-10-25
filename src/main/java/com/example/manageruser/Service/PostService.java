package com.example.manageruser.Service;

import com.example.manageruser.Model.Post;
import com.example.manageruser.Repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
    public List<Post> getPostsByFriendShip(int userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findPostsByFriendship(userId, pageable).getContent();
    }


    public Post findPostById(Long id) {
        Optional<Post> post = postRepository.findById(id);
        return post.orElse(null);
    }

    public void save(Post post) {
        postRepository.save(post);
    }

    public List<Post> findPostsByUID(int id) {
        return postRepository.getPostsByUserId(id); // Thay đổi trả về List<Post>
    }
}
