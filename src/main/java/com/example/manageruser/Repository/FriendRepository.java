package com.example.manageruser.Repository;

import com.example.manageruser.Model.FriendShip;
import com.example.manageruser.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<FriendShip, Long> {
    @Query("SELECT f.friend FROM FriendShip f WHERE f.user.username = :username")
    List<User> findFriendsByUsername(@Param("username") String username);
}