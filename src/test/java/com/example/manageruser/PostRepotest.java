package com.example.manageruser;

import com.example.manageruser.Model.Post;
import com.example.manageruser.Model.User;
import com.example.manageruser.Repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class PostRepotest {
    @Autowired
    private PostRepository postRepository;

    @Test
    public void testFindAll() {

        Iterable<Post> allPost = postRepository.findAll();
        assertThat(allPost).hasSizeGreaterThan(1);

        for(Post post : allPost)
        {
            System.out.println(post);
        }
    }

    @Test
    public void delete()
    {
        Long id = 100L;
        postRepository.deleteById(id);
        Optional<Post> findP = postRepository.findById(id);
        assertThat(findP).isNotPresent();

    }
}
