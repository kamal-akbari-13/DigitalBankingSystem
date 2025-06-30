package com.paykaro.controller;

import com.paykaro.dto.AccountDTO;
import com.paykaro.exception.*;
import com.paykaro.model.Account;
import com.paykaro.model.Beneficiary;
import com.paykaro.security.JwtUtil;
import com.paykaro.service.AccountService;
import com.paykaro.service.BeneficiaryService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class AccountController {

	@Autowired
	private AccountService accountService;

	@Autowired
	private BeneficiaryService beneficiaryService;

	@Autowired
	private JwtUtil jwtUtil;

	private String extractMobileNoFromToken(final HttpServletRequest request) {
		val authorizationHeader = request.getHeader("Authorization");
		val token = authorizationHeader.substring(7);
		return jwtUtil.extractUsername(token);
	}

	// Add new account
	@PostMapping("/account")
	public ResponseEntity<Account> addAccountHandler(
			@RequestBody final AccountDTO accountDTO,
			final HttpServletRequest request
	) throws CustomerException, WalletException {

		val mobileNo = extractMobileNoFromToken(request);

		val account = new Account();
		account.setAccountNo(accountDTO.getAccountNo());
		account.setIfscCode(accountDTO.getIfscCode());
		account.setBankName(accountDTO.getBankName());
		account.setBalance(accountDTO.getBalance());

		val savedAccount = accountService.addAccount(account, mobileNo);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedAccount);
	}

	// Delete account
	@DeleteMapping("/account")
	public ResponseEntity<Account> removeAccountHandler(
			@RequestParam final Integer accId,
			final HttpServletRequest request
	) throws CustomerException, AccountException, WalletException {

		val mobileNo = extractMobileNoFromToken(request);
		val deletedAccount = accountService.removeAccount(accId, mobileNo);
		return ResponseEntity.ok(deletedAccount);
	}

	// View all accounts
	@GetMapping
	public ResponseEntity<List<Account>> viewAccount(
			@RequestParam final Integer walletId,
			final HttpServletRequest request
	) throws CustomerException, WalletException, AccountException {

		val mobileNo = extractMobileNoFromToken(request);
		val accounts = accountService.viewAllAccount(walletId, mobileNo);
		return ResponseEntity.ok(accounts);
	}

	//  Deposit into account
	@PostMapping("/account/deposit")
	public ResponseEntity<String> depositIntoAccount(
			@RequestParam final Integer accountId,
			@RequestParam final Double amount,
			final HttpServletRequest request
	) throws CustomerException, AccountException {

		val mobileNo = extractMobileNoFromToken(request);
		accountService.depositIntoAccount(mobileNo, accountId, amount);
		return ResponseEntity.ok("Amount deposited into account successfully");
	}

	//  Transfer from Account to Wallet
	@PostMapping("/account/transferToWallet")
	public ResponseEntity<String> transferFromAccountToWallet(
			@RequestParam final Integer accountId,
			@RequestParam final Double amount,
			final HttpServletRequest request
	) throws CustomerException, AccountException, WalletException {

		val mobileNo = extractMobileNoFromToken(request);
		accountService.transferFromAccountToWallet(mobileNo, accountId, amount);
		return ResponseEntity.ok(" Amount transferred from account to wallet successfully");
	}

	//  Transfer between your own accounts
	@PostMapping("/account/transferBetweenAccounts")
	public ResponseEntity<String> transferBetweenAccounts(
			@RequestParam final Integer sourceAccountId,
			@RequestParam final Integer targetAccountId,
			@RequestParam final Double amount,
			final HttpServletRequest request
	) throws CustomerException, AccountException {

		val mobileNo = extractMobileNoFromToken(request);
		accountService.transferBetweenAccounts(mobileNo, sourceAccountId, targetAccountId, amount);
		return ResponseEntity.ok(" Amount transferred between your accounts successfully");
	}

	//  Transfer to another customer’s account
	@PostMapping("/account/transferToOtherCustomerAccount")
	public ResponseEntity<String> transferAccountToAccountOtherCustomer(
			@RequestParam final Integer sourceAccountId,
			@RequestParam final Integer targetAccountId,
			@RequestParam final Double amount,
			final HttpServletRequest request
	) throws CustomerException, AccountException {

		val mobileNo = extractMobileNoFromToken(request);
		accountService.transferAccountToAccountOtherCustomer(mobileNo, sourceAccountId, targetAccountId, amount);
		return ResponseEntity.ok(" Amount transferred successfully to other customer's account");
	}

	// Transfer to Beneficiary
	@PostMapping("/account/transferToBeneficiary")
	public ResponseEntity<String> transferFromAccountToBeneficiary(
			@RequestParam final Integer sourceAccountId,
			@RequestParam final Integer beneficiaryId,
			@RequestParam final Double amount,
			final HttpServletRequest request
	) throws CustomerException, AccountException, BeneficiaryException {

		val mobileNo = extractMobileNoFromToken(request);
		val beneficiary = beneficiaryService.getBeneficiaryByIdAndCustomer(mobileNo, beneficiaryId);
		accountService.transferFromAccountToBeneficiary(mobileNo, sourceAccountId, beneficiary, amount);
		return ResponseEntity.ok(" Amount transferred from account to beneficiary successfully");
	}
}
