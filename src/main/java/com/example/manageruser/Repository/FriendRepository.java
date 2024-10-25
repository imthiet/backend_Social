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

    // Kiểm tra xem hai người dùng có tồn tại quan hệ kết bạn hay chưa
    @Query("SELECT COUNT(f) > 0 FROM FriendShip f WHERE (f.user = :currentUser AND f.friend = :friendUser) OR (f.user = :friendUser AND f.friend = :currentUser)")
    boolean existsBetweenUsers(@Param("currentUser") User currentUser, @Param("friendUser") User friendUser);


    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
            "FROM FriendShip f WHERE f.user = :currentUser AND f.friend = :friendUser AND f.accepted = false")
    boolean existsByUserAndFriend(@Param("currentUser") User currentUser, @Param("friendUser") User friendUser);

    @Query("SELECT COUNT(f) > 0 FROM FriendShip f WHERE f.user = :currentUser AND f.friend = :friendUser AND f.accepted = true")
    boolean existsByUserAndFriendAndAccepted(@Param("currentUser") User currentUser, @Param("friendUser") User friendUser, @Param("accepted") boolean accepted);

    FriendShip findByUserAndFriend(User user, User friend);



}


