package com.example.manageruser.Repository;

import com.example.manageruser.Model.Chat;
import com.example.manageruser.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findByParticipantsUsername(String username);
}
