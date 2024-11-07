package com.example.manageruser.Repository;

import com.example.manageruser.Model.Message;
import com.example.manageruser.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
//    List<Message> findBySenderAndReceiver(User sender, User receiver);
//    List<Message> findByChatIdAndSenderAndReceiver(Long chatId, User sender, User receiver);

    Page<Message> findByChatId(Long chatId, Pageable pageable);


    List<Message> findByChatIdOrderByTimestampAsc(Long chatId);
}

