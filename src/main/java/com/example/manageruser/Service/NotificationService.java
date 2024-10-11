package com.example.manageruser.Service;

import com.example.manageruser.Dto.NotificationDTO;
import com.example.manageruser.Model.Notification;
import com.example.manageruser.Repository.NotificationRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<NotificationDTO> getNotificationsForUser(String username) {
        List<Notification> notifications = notificationRepository.findByReceiverUsername(username);

        // Chuyển đổi từ Notification sang NotificationDTO
        return notifications.stream()
                .map(notification -> new NotificationDTO(notification.getId(), notification.getContent(), notification.getReceiver().getUsername()))
                .collect(Collectors.toList());
    }

//    public void save(Notification notification) {
//        notificationRepository.save(notification);
//    }
//    // Tìm thông báo cho người nhận
//    public List<Notification> findByReceiverUsername(String username) {
//        return notificationRepository.findByReceiverUsername(username);
//    }

    // Lưu thông báo và trả về thông báo đã lưu
    public Notification save(Notification notification) {
        return notificationRepository.save(notification); // Phương thức này phải trả về Notification
    }

    // Tìm thông báo cho người nhận
    public List<Notification> findByReceiverUsername(String username) {
        return notificationRepository.findByReceiver_Username(username);
    }
}
