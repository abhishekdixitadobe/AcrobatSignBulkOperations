package com.adobe.acrobatsign.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.adobe.acrobatsign.entity.ParticipantList;
import com.adobe.acrobatsign.entity.ParticipantRepository;

@Service
public class ParticipantListService {
	private final ParticipantRepository participantRepository;

    public ParticipantListService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }
    
    public Iterable<ParticipantList> list() {
        return participantRepository.findAll();
    }

    public ParticipantList save(ParticipantList detail) {
        return participantRepository.save(detail);
    }

    public void save(List<ParticipantList> details) {
    	participantRepository.saveAll(details);
    }
}
