package com.example.manageruser.RestAPI;

import com.example.manageruser.Dto.ReportPostRequest;
import com.example.manageruser.Model.ReportedPost;
import com.example.manageruser.Service.ReportedPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportedPostController {

    @Autowired
    private ReportedPostService reportedPostService;

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
            // Xóa các báo cáo liên quan đến bài viết
            reportedPostService.deletePost(postId);

            // Trả về thông báo thành công
            return ResponseEntity.ok("Post deleted successfully");
        } catch (Exception e) {
            // Xử lý lỗi nếu có
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting post");
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

