package com.example.manageruser.Repository;

import com.example.manageruser.Model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Integer> {
    User findByemail(String email);

    User findByUsername(String username);

    List<User> findByUsernameContainingIgnoreCase(String username);


}
