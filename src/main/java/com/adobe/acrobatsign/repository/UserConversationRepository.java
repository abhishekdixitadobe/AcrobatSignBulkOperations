package com.adobe.acrobatsign.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.adobe.acrobatsign.model.User;
import com.adobe.acrobatsign.model.UserConversation;

public interface UserConversationRepository extends JpaRepository<UserConversation, Long> {

	List<UserConversation> findByUser(User user);
	
	@Modifying
    @Transactional
    @Query(value = "UPDATE users_conversations SET conversations = jsonb_set(conversations, '-1', :jsonConversations::jsonb) WHERE id = :id", nativeQuery = true)
    int updateConversation(@Param("id") Long id, @Param("jsonConversations") String jsonConversations);
	
}
