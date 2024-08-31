package com.example.manageruser.Repository;

import com.example.manageruser.Model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface PostRepository extends JpaRepository<Post,Long> {
    // Truy vấn để lấy các bài đăng của bạn bè đã được chấp nhận (accepted = 1)
    @Query("SELECT p FROM Post p " +
            "JOIN p.user u " +
            "JOIN FriendShip f ON (f.user.id = u.id OR f.friend.id = u.id) " +
            "JOIN User cu ON (cu.id = f.user.id OR cu.id = f.friend.id) " +
            "WHERE cu.username = :username " +
            "AND f.accepted = true " +
            "AND u.username != :username")
   List<Post> findPostsByFriendship(@Param("username") String username);
//    Page<Post> findPostsByFriendship(@Param("username") String username, Pageable pageable);


}


