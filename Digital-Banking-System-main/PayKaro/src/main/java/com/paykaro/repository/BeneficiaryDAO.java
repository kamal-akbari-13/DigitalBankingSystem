package com.paykaro.repository;

import com.paykaro.model.Beneficiary;
import com.paykaro.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeneficiaryDAO extends JpaRepository<Beneficiary, Integer> {

	List<Beneficiary> findByCustomer(final Customer customer);

}
