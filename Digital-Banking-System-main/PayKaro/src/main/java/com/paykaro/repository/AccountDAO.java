package com.paykaro.repository;

import com.paykaro.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountDAO extends JpaRepository<Account, Integer> {

    Account findByAccountNoAndIfscCode(final String accountNo, final String ifscCode);

}
