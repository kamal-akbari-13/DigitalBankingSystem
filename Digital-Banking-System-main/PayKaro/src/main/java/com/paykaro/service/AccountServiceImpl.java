package com.paykaro.service;

import com.paykaro.exception.*;
import com.paykaro.model.*;
import com.paykaro.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountDAO accountDAO;

	@Autowired
	private CustomerDAO customerDAO;

	@Autowired
	private WalletDAO walletDAO;

	@Autowired
	private TransactionHistoryService transactionHistoryService;

	@Transactional
	@Override
	public Account addAccount(final Account account, final String mobileNo) throws CustomerException, WalletException {
		final Customer customer = customerDAO.findByMobileNo(mobileNo);
		if (customer == null) throw new CustomerException("Invalid Mobile No");

		final Wallet wallet = customer.getWallet();
		if (wallet == null) throw new WalletException("No wallet found");

		account.setWallet(wallet);
		final Account savedAccount = accountDAO.save(account);
		wallet.getAccounts().add(savedAccount);
		walletDAO.save(wallet);
		return savedAccount;
	}

	@Transactional
	@Override
	public Account removeAccount(final Integer accId, final String mobileNo) throws CustomerException, AccountException, WalletException {
		final Customer customer = customerDAO.findByMobileNo(mobileNo);
		if (customer == null) throw new CustomerException("Invalid Mobile No");

		final Account existingAccount = accountDAO.findById(accId)
				.orElseThrow(() -> new AccountException("No account found"));

		final Wallet wallet = customer.getWallet();
		if (!existingAccount.getWallet().equals(wallet)) {
			throw new AccountException("This account does not belong to you");
		}

		accountDAO.delete(existingAccount);
		return existingAccount;
	}

	@Override
	public List<Account> viewAllAccount(final Integer walletId, final String mobileNo) throws CustomerException, WalletException, AccountException {
		final Customer customer = customerDAO.findByMobileNo(mobileNo);
		if (customer == null) throw new CustomerException("Invalid Mobile No");

		final Wallet wallet = customer.getWallet();
		if (!wallet.getWid().equals(walletId)) {
			throw new WalletException("Unauthorized wallet access");
		}

		if (wallet.getAccounts().isEmpty()) {
			throw new AccountException("No account found");
		}

		return wallet.getAccounts();
	}

	@Transactional
	@Override
	public void depositIntoAccount(final String mobileNo, final Integer accountId, final Double amount) throws CustomerException, AccountException {
		final Customer customer = customerDAO.findByMobileNo(mobileNo);
		if (customer == null) throw new CustomerException("Customer not found");

		final Account account = accountDAO.findById(accountId)
				.orElseThrow(() -> new AccountException("Account not found"));

		if (!account.getWallet().equals(customer.getWallet())) {
			throw new AccountException("This account doesn't belong to you");
		}

		account.setBalance(account.getBalance() + amount);
		accountDAO.save(account);

		transactionHistoryService.recordTransaction(
				customer, "CREDIT", "ACCOUNT", account.getAccId(),
				null, null, amount, "Account Deposit",
				null, null
		);
	}

	@Transactional
	@Override
	public void transferFromAccountToWallet(final String mobileNo, final Integer accountId, final Double amount)
			throws CustomerException, AccountException, WalletException {

		final Customer customer = customerDAO.findByMobileNo(mobileNo);
		if (customer == null) throw new CustomerException("Customer not found");

		final Account account = accountDAO.findById(accountId)
				.orElseThrow(() -> new AccountException("Account not found"));

		if (!account.getWallet().equals(customer.getWallet())) {
			throw new AccountException("This account doesn't belong to you");
		}

		if (amount > account.getBalance()) throw new AccountException("Insufficient balance");

		account.setBalance(account.getBalance() - amount);
		accountDAO.save(account);

		final Wallet wallet = customer.getWallet();
		wallet.setBalance(wallet.getBalance() + amount);
		walletDAO.save(wallet);

		transactionHistoryService.recordTransaction(
				customer, "DEBIT", "ACCOUNT", account.getAccId(),
				"WALLET", wallet.getWid(), amount,
				"Account to Wallet Transfer",
				null, null
		);
	}

	@Transactional
	@Override
	public void transferBetweenAccounts(final String mobileNo, final Integer sourceAccountId, final Integer targetAccountId, final Double amount)
			throws CustomerException, AccountException {

		final Customer customer = customerDAO.findByMobileNo(mobileNo);
		if (customer == null) throw new CustomerException("Customer not found");

		final Account sourceAccount = accountDAO.findById(sourceAccountId)
				.orElseThrow(() -> new AccountException("Source account not found"));

		final Account targetAccount = accountDAO.findById(targetAccountId)
				.orElseThrow(() -> new AccountException("Target account not found"));

		if (!sourceAccount.getWallet().equals(customer.getWallet()) || !targetAccount.getWallet().equals(customer.getWallet())) {
			throw new AccountException("Both accounts must belong to your wallet");
		}

		if (amount > sourceAccount.getBalance()) {
			throw new AccountException("Insufficient balance");
		}

		sourceAccount.setBalance(sourceAccount.getBalance() - amount);
		targetAccount.setBalance(targetAccount.getBalance() + amount);
		accountDAO.save(sourceAccount);
		accountDAO.save(targetAccount);

		transactionHistoryService.recordTransaction(
				customer, "DEBIT", "ACCOUNT", sourceAccount.getAccId(),
				"ACCOUNT", targetAccount.getAccId(), amount,
				"Account to Account Transfer (Self)",
				null, null
		);

		transactionHistoryService.recordTransaction(
				customer, "CREDIT", "ACCOUNT", targetAccount.getAccId(),
				null, null, amount,
				"Received from your own Account Transfer",
				null, sourceAccount.getAccId().toString()
		);
	}

	@Transactional
	@Override
	public void transferAccountToAccountOtherCustomer(final String mobileNo, final Integer sourceAccountId, final Integer targetAccountId, final Double amount)
			throws CustomerException, AccountException {

		final Customer sourceCustomer = customerDAO.findByMobileNo(mobileNo);
		if (sourceCustomer == null) throw new CustomerException("Customer not found");

		final Account sourceAccount = accountDAO.findById(sourceAccountId)
				.orElseThrow(() -> new AccountException("Source account not found"));

		final Account targetAccount = accountDAO.findById(targetAccountId)
				.orElseThrow(() -> new AccountException("Target account not found"));

		if (!sourceAccount.getWallet().equals(sourceCustomer.getWallet())) {
			throw new AccountException("Source account does not belong to you");
		}

		if (amount > sourceAccount.getBalance()) throw new AccountException("Insufficient balance");

		sourceAccount.setBalance(sourceAccount.getBalance() - amount);
		targetAccount.setBalance(targetAccount.getBalance() + amount);
		accountDAO.save(sourceAccount);
		accountDAO.save(targetAccount);

		transactionHistoryService.recordTransaction(
				sourceCustomer, "DEBIT", "ACCOUNT", sourceAccount.getAccId(),
				"ACCOUNT", targetAccount.getAccId(), amount,
				"Transfer to Other Customer Account",
				null, null
		);

		final Customer targetCustomer = targetAccount.getWallet().getCustomer();
		transactionHistoryService.recordTransaction(
				targetCustomer, "CREDIT", "ACCOUNT", targetAccount.getAccId(),
				null, null, amount,
				"Received from another customer",
				sourceCustomer.getMobileNo(), sourceAccount.getAccId().toString()
		);
	}

	@Transactional
	@Override
	public void transferFromAccountToBeneficiary(final String mobileNo, final Integer sourceAccountId, final Beneficiary beneficiary, final Double amount)
			throws CustomerException, AccountException {

		final Customer sourceCustomer = customerDAO.findByMobileNo(mobileNo);
		if (sourceCustomer == null) throw new CustomerException("Customer not found");

		final Account sourceAccount = accountDAO.findById(sourceAccountId)
				.orElseThrow(() -> new AccountException("Source account not found"));

		if (!sourceAccount.getWallet().equals(sourceCustomer.getWallet())) {
			throw new AccountException("Source account doesn't belong to you");
		}

		if (amount > sourceAccount.getBalance()) throw new AccountException("Insufficient balance");

		sourceAccount.setBalance(sourceAccount.getBalance() - amount);
		accountDAO.save(sourceAccount);

		final Account targetAccount = accountDAO.findById(beneficiary.getTargetAccountId())
				.orElseThrow(() -> new AccountException("Beneficiary target account not found"));

		targetAccount.setBalance(targetAccount.getBalance() + amount);
		accountDAO.save(targetAccount);

		transactionHistoryService.recordTransaction(
				sourceCustomer, "DEBIT", "ACCOUNT", sourceAccount.getAccId(),
				"ACCOUNT", targetAccount.getAccId(), amount,
				"Account to Beneficiary Transfer",
				null, null
		);

		final Customer targetCustomer = targetAccount.getWallet().getCustomer();
		transactionHistoryService.recordTransaction(
				targetCustomer, "CREDIT", "ACCOUNT", targetAccount.getAccId(),
				null, null, amount,
				"Received from Beneficiary Transfer",
				sourceCustomer.getMobileNo(), sourceAccount.getAccId().toString()
		);
	}
}
