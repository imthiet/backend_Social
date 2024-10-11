package com.example.manageruser.Repository;



import com.example.manageruser.Model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;


@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverUsername(String username);


        List<Notification> findByReceiver_Username(String username); // Sử dụng underscore để truy vấn




}
