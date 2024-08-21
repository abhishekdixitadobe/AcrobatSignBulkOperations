package com.adobe.acrobatsign.entity;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<AgreementDetail, String> {

}
