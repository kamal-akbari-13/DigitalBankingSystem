package com.paykaro.repository;

import com.paykaro.model.BillPayment;
import com.paykaro.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillPaymentDAO extends JpaRepository<BillPayment, Integer> {

	List<BillPayment> findByWallet(final Wallet wallet);
}
