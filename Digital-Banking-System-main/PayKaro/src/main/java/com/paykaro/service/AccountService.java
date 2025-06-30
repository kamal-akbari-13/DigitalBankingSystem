package com.paykaro.service;

import java.util.List;

import com.paykaro.exception.AccountException;
import com.paykaro.exception.CustomerException;
import com.paykaro.exception.WalletException;
import com.paykaro.model.Account;
import com.paykaro.model.Beneficiary;

public interface AccountService {

	// Create account and link to wallet
	Account addAccount(final Account account, final String mobileNo) throws CustomerException, WalletException;

	// Remove account from wallet
	Account removeAccount(final Integer accId, final String mobileNo) throws CustomerException, AccountException, WalletException;

	// View all accounts linked to the given wallet
	List<Account> viewAllAccount(final Integer walletId, final String mobileNo) throws CustomerException, WalletException, AccountException;

	// Deposit amount into a specific account
	void depositIntoAccount(final String mobileNo, final Integer accountId, final Double amount) throws CustomerException, AccountException;

	// Transfer money from account to wallet
	void transferFromAccountToWallet(final String mobileNo, final Integer accountId, final Double amount)
			throws CustomerException, AccountException, WalletException;

	// Transfer between two accounts of same customer
	void transferBetweenAccounts(final String mobileNo, final Integer sourceAccountId, final Integer targetAccountId, final Double amount)
			throws CustomerException, AccountException;

	// Transfer to another customer’s account
	void transferAccountToAccountOtherCustomer(final String mobileNo, final Integer sourceAccountId, final Integer targetAccountId, final Double amount)
			throws CustomerException, AccountException;

	// Transfer to beneficiary account
	void transferFromAccountToBeneficiary(final String mobileNo, final Integer sourceAccountId, final Beneficiary beneficiary, final Double amount)
			throws CustomerException, AccountException;
}
