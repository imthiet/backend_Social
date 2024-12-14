package com.example.manageruser.RestAPI;

import com.example.manageruser.Dto.ReportPostRequest;
import com.example.manageruser.Model.*;
import com.example.manageruser.Service.NotificationService;
import com.example.manageruser.Service.PostService;
import com.example.manageruser.Service.ReportedPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportedPostController {

    @Autowired
    private ReportedPostService reportedPostService;
    @Autowired
    private PostService postService;

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/report")
    public ResponseEntity<String> reportPost(@RequestBody ReportPostRequest reportRequest) {
        boolean success = reportedPostService.reportPost(reportRequest.getPostId(), reportRequest.getReportedBy(), reportRequest.getReason());
        if (success) {
            return ResponseEntity.ok("Report successfully submitted.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to report post. Post not found.");
        }
    }

    @GetMapping
    public ResponseEntity<Page<ReportPostRequest>> getAllReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<ReportPostRequest> reports = reportedPostService.getAllReports(page, size);
        return ResponseEntity.ok(reports);
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        try {
            // Lấy bài viết cần xóa
            Post post = postService.findPostById(postId);
            if (post == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
            }

            // Lấy danh sách các báo cáo liên quan đến bài viết
            List<ReportedPost> reportedPosts = reportedPostService.findByPostId(postId);

            // Lấy thông tin tác giả bài viết
            User author = post.getUser();

            // Tạo thông báo cho tác giả
            Notification authorNotification = new Notification();
            authorNotification.setContentnoti("Your post has been deleted by an admin.");
            authorNotification.setType(NotificationType.LIKE_COMMENT_SHARE);
            authorNotification.setSender(null); // Admin
            authorNotification.setReceiver(author);
            authorNotification.setStatus("unread");
            authorNotification.setTimestamp(LocalDateTime.now());
            notificationService.save(authorNotification);

            // Tạo thông báo cho từng người đã báo cáo bài viết


            // Xóa bài viết và các báo cáo liên quan
            reportedPostService.deleteByPostId(postId);
            postService.deleteById(postId);

            return ResponseEntity.ok("Post deleted successfully!");
        } catch (Exception e) {
            // Xử lý lỗi
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting post");
        }
    }



    @DeleteMapping("/ignore/{reportId}")
    public ResponseEntity<String> ignorePost(@PathVariable Long reportId) {
        try {


            boolean isDeleted = reportedPostService.removeReport(reportId);
            if (isDeleted) {
                return ResponseEntity.ok("Post with ID " + reportId + " has been ignored successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Post with ID " + reportId + " was not found in the reported list.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while trying to ignore the post.");
        }
    }


}
