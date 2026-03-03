package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.ChatMessage;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {

    @Query("SELECT c FROM ChatMessage c WHERE (c.senderId = :user1 AND c.receiverId = :user2) OR (c.senderId = :user2 AND c.receiverId = :user1) ORDER BY c.timestamp ASC")
    List<ChatMessage> findConversation(@Param("user1") int user1, @Param("user2") int user2);

    @Query("SELECT DISTINCT c.senderId FROM ChatMessage c WHERE c.receiverId = :userId " +
            "UNION " +
            "SELECT DISTINCT c.receiverId FROM ChatMessage c WHERE c.senderId = :userId")
    List<Integer> findChattedUserIds(@Param("userId") int userId);

    // Notifications
    long countByReceiverIdAndIsReadFalse(int receiverId);
}
