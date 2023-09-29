package com.adobe.acrobatsign.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adobe.acrobatsign.model.User;
import com.adobe.acrobatsign.model.UserConversation;

public interface UserConversationRepository extends JpaRepository<UserConversation, Long> {

	UserConversation findByUser(User user);
}
