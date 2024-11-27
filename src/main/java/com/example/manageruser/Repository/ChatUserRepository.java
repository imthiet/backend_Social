package com.example.manageruser.Repository;

import com.example.manageruser.Model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatUserRepository extends JpaRepository<Chat,Long> {

    @Modifying
    @Query(value = "DELETE FROM chat_user WHERE chat_id = :chatId", nativeQuery = true)
    void deleteByChatId(Long chatId);
}
