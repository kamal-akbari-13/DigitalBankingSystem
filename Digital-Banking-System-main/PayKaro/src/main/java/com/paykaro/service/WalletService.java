package com.paykaro.service;

import com.paykaro.exception.AccountException;
import com.paykaro.exception.CustomerException;
import com.paykaro.exception.WalletException;
import com.paykaro.model.Beneficiary;
import com.paykaro.model.Customer;

public interface WalletService {

	Customer showBalance(final String mobileNo) throws CustomerException;

	// Transfer from wallet to any normal account
	void transferWalletToAccount(
			final String sourceMobileNo,
			final Integer targetAccountId,
			final Double amount
	) throws CustomerException, AccountException, WalletException;

	// Transfer from wallet to any registered beneficiary
	void transferWalletToBeneficiary(
			final String mobileNo,
			final Beneficiary beneficiary,
			final Double amount
	) throws CustomerException, WalletException, AccountException;
}
