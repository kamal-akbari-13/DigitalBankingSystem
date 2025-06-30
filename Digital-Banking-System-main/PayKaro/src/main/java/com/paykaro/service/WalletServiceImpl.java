package com.paykaro.service;

import com.paykaro.exception.*;
import com.paykaro.model.*;
import com.paykaro.repository.*;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletServiceImpl implements WalletService {

	@Autowired
	private WalletDAO walletDAO;

	@Autowired
	private CustomerDAO customerDAO;

	@Autowired
	private AccountDAO accountDAO;

	@Autowired
	private TransactionHistoryService transactionHistoryService;

	@Override
	public Customer showBalance(final String mobileNo) throws CustomerException {
		final val customer = customerDAO.findByMobileNo(mobileNo);
		if (customer == null) {
			throw new CustomerException("Customer not found");
		}
		return customer;
	}

	@Transactional
	@Override
	public void transferWalletToAccount(final String sourceMobileNo, final Integer targetAccountId, final Double amount)
			throws CustomerException, AccountException, WalletException {

		final val sourceCustomer = customerDAO.findByMobileNo(sourceMobileNo);
		if (sourceCustomer == null) throw new CustomerException("Source customer not found");

		final val sourceWallet = sourceCustomer.getWallet();
		if (sourceWallet == null) throw new WalletException("Wallet not found for source customer");

		final val targetAccount = accountDAO.findById(targetAccountId)
				.orElseThrow(() -> new AccountException("Target account not found"));

		if (sourceWallet.getWid().equals(targetAccount.getWallet().getWid())) {
			throw new WalletException("Transfer from wallet to your own linked account is not supported");
		}

		if (amount > sourceWallet.getBalance()) {
			throw new WalletException("Insufficient wallet balance");
		}

		final val targetCustomer = targetAccount.getWallet().getCustomer();

		sourceWallet.setBalance(sourceWallet.getBalance() - amount);
		walletDAO.save(sourceWallet);

		targetAccount.setBalance(targetAccount.getBalance() + amount);
		accountDAO.save(targetAccount);

		// Sender - DEBIT
		transactionHistoryService.recordTransaction(
				sourceCustomer, "DEBIT", "WALLET", sourceWallet.getWid(),
				"ACCOUNT", targetAccount.getAccId(),
				amount, "Wallet to Account Transfer",
				null, null
		);

		// Receiver - CREDIT
		transactionHistoryService.recordTransaction(
				targetCustomer, "CREDIT", "ACCOUNT", targetAccount.getAccId(),
				null, null, amount,
				"Received from Wallet Transfer",
				sourceCustomer.getMobileNo(), null
		);
	}

	@Transactional
	@Override
	public void transferWalletToBeneficiary(final String mobileNo, final Beneficiary beneficiary, final Double amount)
			throws CustomerException, WalletException, AccountException {

		final val sourceCustomer = customerDAO.findByMobileNo(mobileNo);
		if (sourceCustomer == null) throw new CustomerException("Customer not found");

		final val wallet = sourceCustomer.getWallet();
		if (wallet == null) throw new WalletException("Wallet not found");

		if (amount > wallet.getBalance()) throw new WalletException("Insufficient wallet balance");

		final val targetAccount = accountDAO.findById(beneficiary.getTargetAccountId())
				.orElseThrow(() -> new AccountException("Beneficiary target account not found"));

		final val targetCustomer = targetAccount.getWallet().getCustomer();

		if (wallet.getWid().equals(targetAccount.getWallet().getWid())) {
			throw new WalletException("Transfer from wallet to your own linked account is not supported");
		}

		wallet.setBalance(wallet.getBalance() - amount);
		walletDAO.save(wallet);

		targetAccount.setBalance(targetAccount.getBalance() + amount);
		accountDAO.save(targetAccount);

		// Sender - DEBIT
		transactionHistoryService.recordTransaction(
				sourceCustomer, "DEBIT", "WALLET", wallet.getWid(),
				"ACCOUNT", targetAccount.getAccId(),
				amount, "Wallet to Beneficiary Transfer",
				null, null
		);

		// Receiver - CREDIT
		transactionHistoryService.recordTransaction(
				targetCustomer, "CREDIT", "ACCOUNT", targetAccount.getAccId(),
				null, null, amount,
				"Received from Beneficiary Transfer",
				sourceCustomer.getMobileNo(), null
		);
	}
}
