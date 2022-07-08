package com.microservice.accounts.repository;

import com.microservice.accounts.model.Accounts;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountsRepository extends CrudRepository<Accounts, Long> {
    Accounts findByCustomerId(Long id);
}
