package com.adobe.acrobatsign.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.adobe.acrobatsign.entity.AgreementDetail;
import com.adobe.acrobatsign.entity.CustomerRepository;

@Service
public class AgreementDetailService {
	
    private final CustomerRepository customerRepository;

    public AgreementDetailService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    
    public Iterable<AgreementDetail> list() {
        return customerRepository.findAll();
    }

    public AgreementDetail save(AgreementDetail detail) {
        return customerRepository.save(detail);
    }

    public void save(List<AgreementDetail> details) {
    	customerRepository.saveAll(details);
    }

  /*  public Iterable<List<AgreementDetails>> list() {
        return customerRepository.findAll();
    }

    public Iterable<AgreementDetails> save(List<AgreementDetails> agreementDetails) {
        return customerRepository.save(agreementDetails);
    }*/

}
