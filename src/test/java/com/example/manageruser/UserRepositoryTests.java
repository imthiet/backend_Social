package com.example.manageruser;

import com.example.manageruser.Repository.UserRepository;
import com.example.manageruser.Model.User;


import com.example.manageruser.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;


import  static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {
    @Autowired private UserRepository repo;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService; // Lớp chứa phương thức registerUser

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testAddNew()
    {
        User user = new User();
        user.setUsername("admin12");
        user.setPassword("1234");
        user.setEmail("thietquang0034@gmail.com");

        User saved_user = repo.save(user);

        assertThat(saved_user).isNotNull();
        assertThat(saved_user.getPassword()).isNotEmpty();


    }
    @Test
    public void testListAll()
    {
        Iterable<User> allUsers = repo.findAll();
        assertThat(allUsers).hasSizeGreaterThan(1);

        for(User user : allUsers)
        {
            System.out.println(user);
        }
    }

    @Test
    public void testUpdate()
    {
        Integer id = 1;
        Optional<User> findU = repo.findById(id);
        User user = findU.get();
        user.setUsername("changed usname");
        repo.save(user);

        User updated_user = repo.findById(id).get();
        assertThat(updated_user.getUsername()).isEqualTo("changed usname");


    }
    @Test
    public void Get()
    {
        Integer id = 3;
        Optional<User> findU = repo.findById(id);
        assertThat(findU).isPresent();
        System.out.println(findU.get());
    }
    @Test
    public void Getname()
    {
        String username = "23A4030326";
        Optional<User> findU = repo.findByUsername(username);
        assertThat(findU).isPresent();
        System.out.println(findU.get());
    }

    @Test
    public void delete()
    {
        Integer id = 3;
        repo.deleteById(id);
        Optional<User> findU = repo.findById(id);
        assertThat(findU).isNotPresent();

    }


}


