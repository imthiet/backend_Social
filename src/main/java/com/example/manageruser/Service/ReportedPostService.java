package com.example.manageruser.Service;

import com.example.manageruser.Dto.ReportPostRequest;
import com.example.manageruser.Model.Post;
import com.example.manageruser.Model.ReportedPost;
import com.example.manageruser.Model.User;
import com.example.manageruser.Repository.PostRepository;
import com.example.manageruser.Repository.ReportedPostRepository;
import com.example.manageruser.Repository.UserRepository;
import com.example.manageruser.WskConfig.BlobUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.sql.Blob;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReportedPostService {

    @Autowired
    private ReportedPostRepository reportedPostRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public boolean reportPost(Long postId, Long reportedById, String reason) {
        try {
            // Lấy bài viết từ database
            Optional<Post> postOptional = postRepository.findById(postId);
            if (postOptional.isEmpty()) {
                return false; // Không tìm thấy bài viết
            }

            // Tạo đối tượng ReportedPost
            ReportedPost reportedPost = ReportedPost.builder()
                    .post(postOptional.get())
                    .reportedBy(reportedById) // Lưu user_id trực tiếp
                    .reason(reason)
                    .reportedAt(new Date()) // Lấy thời gian hiện tại
                    .build();

            // Lưu thông tin báo cáo vào database
            reportedPostRepository.save(reportedPost);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Page<ReportPostRequest> getAllReports(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReportedPost> reports = reportedPostRepository.findAll(pageable);
        return reports.map(this::convertToDTO);  // Chuyển đổi từng báo cáo sang DTO
    }

    public void deletePost(Long postId) {
        // Trước khi xóa bài viết, cần xóa các bản ghi liên quan trong bảng reported_post
        List<ReportedPost> reportedPosts = reportedPostRepository.findByPostId(postId);
        if (reportedPosts != null) {
            reportedPostRepository.deleteAll(reportedPosts);  // Xóa tất cả báo cáo liên quan
        }

        // Sau đó, xóa bài viết
        postRepository.deleteById(postId);
    }

    public boolean removeReport(Long reportId) {
        if (reportedPostRepository.existsById(reportId)) {
            reportedPostRepository.deleteById(reportId);
            return true;
        }
        return false;
    }


    private ReportPostRequest convertToDTO(ReportedPost reportedPost) {
        ReportPostRequest dto = new ReportPostRequest();
        Post post = reportedPost.getPost();

        // Lấy thông tin từ Post
        dto.setReportId(reportedPost.getId());
        dto.setPostId(post.getId());
        dto.setReportedBy(reportedPost.getReportedBy());
        dto.setReason(reportedPost.getReason());

        // Thêm nội dung bài viết
        dto.setPostContent(post.getContent());  // Đảm bảo bạn có trường này trong DTO

        // Lấy ảnh bài viết (Base64)
        if (post.getPng() != null) {
            dto.setPostImage(BlobUtil.blobToBase64(post.getPng()));  // Giả sử bạn đã có phương thức này để chuyển Blob thành Base64
        }

        return dto;
    }


    public void deleteByPostId(Long postId) {
        reportedPostRepository.deleteById(postId);
    }

    public List<ReportedPost> findByPostId(Long postId) {
        return reportedPostRepository.findByPostId(postId);
    }
}