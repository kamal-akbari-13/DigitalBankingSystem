package com.paykaro.service;

import java.util.List;

import com.paykaro.exception.BillPaymentException;
import com.paykaro.exception.CustomerException;
import com.paykaro.exception.WalletException;
import com.paykaro.model.BillPayment;
import com.paykaro.model.Customer;
import com.paykaro.model.Wallet;
import com.paykaro.repository.BillPaymentDAO;
import com.paykaro.repository.CustomerDAO;
import com.paykaro.repository.WalletDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.val;

@Service
public class BillPaymentServiceImpl implements BillPaymentService {

	@Autowired
	private BillPaymentDAO billPaymentDAO;

	@Autowired
	private CustomerDAO customerDAO;

	@Autowired
	private WalletDAO walletDAO;

	@Transactional
	@Override
	public BillPayment addBillPayment(final BillPayment billPayment, final String mobileNo)
			throws BillPaymentException, CustomerException, WalletException {

		val customer = customerDAO.findByMobileNo(mobileNo);
		if (customer == null) {
			throw new CustomerException("Customer not found");
		}

		val wallet = customer.getWallet();
		if (wallet == null) {
			throw new WalletException("Wallet not found");
		}

		val paymentAmount = billPayment.getAmount();
		if (paymentAmount > wallet.getBalance()) {
			throw new WalletException("Insufficient wallet balance for bill payment");
		}

		// Deduct from wallet
		wallet.setBalance(wallet.getBalance() - paymentAmount);
		walletDAO.save(wallet);

		// Link bill to wallet
		billPayment.setWallet(wallet);

		return billPaymentDAO.save(billPayment);
	}

	@Override
	public List<BillPayment> viewAllBillPayments(final String mobileNo)
			throws CustomerException, WalletException, BillPaymentException {

		val customer = customerDAO.findByMobileNo(mobileNo);
		if (customer == null) {
			throw new CustomerException("Customer not found");
		}

		val wallet = customer.getWallet();
		if (wallet == null) {
			throw new WalletException("Wallet not found");
		}

		val billPayments = billPaymentDAO.findByWallet(wallet);
		if (billPayments.isEmpty()) {
			throw new BillPaymentException("No bill payments found");
		}

		return billPayments;
	}
}
