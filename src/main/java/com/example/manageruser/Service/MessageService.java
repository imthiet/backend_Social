package com.example.manageruser.Service;

import com.example.manageruser.Model.Message;
import com.example.manageruser.Repository.LikeRepository;
import com.example.manageruser.Repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    public Message save(Message message) {
        return messageRepository.save(message);
    }
}
