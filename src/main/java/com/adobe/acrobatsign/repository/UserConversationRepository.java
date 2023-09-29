package com.adobe.acrobatsign.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adobe.acrobatsign.model.User;
import com.adobe.acrobatsign.model.UserConversation;

public interface UserConversationRepository extends JpaRepository<UserConversation, Long> {

	List<UserConversation> findByUser(User user);
}
