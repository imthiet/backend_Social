package com.example.manageruser.Repository;

import com.example.manageruser.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SearchRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE CONCAT(u.username, ' ', u.email) LIKE %?1%")

    public List<User> search(String keyword);

    public User findUserByUsername(String username);
}
