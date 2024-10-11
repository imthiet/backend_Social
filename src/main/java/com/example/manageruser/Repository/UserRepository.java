package com.example.manageruser.Repository;

import com.example.manageruser.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    //User findByemail(String email);

    //    User findByUsername(String username);
    Optional<User> findByUsername(String username);


    List<User> findByUsernameContainingIgnoreCase(String username);

    User findByVerificationCode(String code);

    Optional<User> findByEmail(String email);

    //    User findByEmail(String email);
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}



