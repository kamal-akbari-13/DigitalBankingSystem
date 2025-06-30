package com.paykaro.service;

import java.util.List;

import com.paykaro.exception.BillPaymentException;
import com.paykaro.exception.CustomerException;
import com.paykaro.exception.WalletException;
import com.paykaro.model.BillPayment;

public interface BillPaymentService {

	BillPayment addBillPayment(
			final BillPayment billPayment,
			final String mobileNo
	) throws BillPaymentException, CustomerException, WalletException;

	List<BillPayment> viewAllBillPayments(
			final String mobileNo
	) throws CustomerException, WalletException, BillPaymentException;
}
