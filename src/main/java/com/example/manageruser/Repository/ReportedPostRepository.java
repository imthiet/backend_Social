package com.example.manageruser.Repository;

import com.example.manageruser.Model.ReportedPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportedPostRepository extends JpaRepository<ReportedPost, Long> {
    List<ReportedPost> findByPostId(Long postId);


    boolean existsById(Long id); // Sử dụng đúng tên phương thức

    void deleteById(Long id); // Sử dụng đúng tên phương thức
}
