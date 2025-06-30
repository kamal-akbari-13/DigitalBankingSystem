package com.paykaro.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paykaro.dto.BeneficiaryDTO;
import com.paykaro.exception.AccountException;
import com.paykaro.exception.BeneficiaryException;
import com.paykaro.exception.CustomerException;
import com.paykaro.model.Account;
import com.paykaro.model.Beneficiary;
import com.paykaro.model.Customer;
import com.paykaro.repository.AccountDAO;
import com.paykaro.repository.BeneficiaryDAO;
import com.paykaro.repository.CustomerDAO;

import lombok.val;

@Service
public class BeneficiaryServiceImpl implements BeneficiaryService {

	@Autowired
	private CustomerDAO customerDAO;

	@Autowired
	private BeneficiaryDAO beneficiaryDAO;

	@Autowired
	private AccountDAO accountDAO;

	@Transactional
	@Override
	public Beneficiary addBeneficiary(final String mobileNo, final BeneficiaryDTO beneficiaryDTO)
			throws CustomerException, AccountException {

		val sourceCustomer = this.customerDAO.findByMobileNo(mobileNo);
		if (sourceCustomer == null) {
			throw new CustomerException("Logged-in customer not found");
		}

		val targetCustomer = this.customerDAO.findByMobileNo(beneficiaryDTO.getTargetCustomerMobile());
		if (targetCustomer == null) {
			throw new CustomerException("Target customer not found");
		}

		val targetAccount = this.accountDAO.findById(beneficiaryDTO.getTargetAccountId())
				.orElseThrow(() -> new AccountException("Target account not found"));

		if (!targetAccount.getWallet().equals(targetCustomer.getWallet())) {
			throw new AccountException("Target account does not belong to the specified target customer");
		}

		val beneficiary = new Beneficiary();
		beneficiary.setBeneficiaryName(beneficiaryDTO.getBeneficiaryName());
		beneficiary.setTargetCustomerMobile(beneficiaryDTO.getTargetCustomerMobile());
		beneficiary.setTargetAccountId(beneficiaryDTO.getTargetAccountId());
		beneficiary.setCustomer(sourceCustomer);

		return this.beneficiaryDAO.save(beneficiary);
	}

	@Override
	public List<Beneficiary> getBeneficiaries(final String mobileNo) throws CustomerException {
		val customer = this.customerDAO.findByMobileNo(mobileNo);
		if (customer == null) {
			throw new CustomerException("Customer not found");
		}
		return this.beneficiaryDAO.findByCustomer(customer);
	}

	@Transactional
	@Override
	public void deleteBeneficiary(final String mobileNo, final Integer beneficiaryId)
			throws CustomerException, BeneficiaryException {

		val customer = this.customerDAO.findByMobileNo(mobileNo);
		if (customer == null) {
			throw new CustomerException("Customer not found");
		}

		val beneficiary = this.beneficiaryDAO.findById(beneficiaryId)
				.orElseThrow(() -> new BeneficiaryException("Beneficiary not found"));

		if (!beneficiary.getCustomer().equals(customer)) {
			throw new BeneficiaryException("Beneficiary does not belong to the current user");
		}

		this.beneficiaryDAO.delete(beneficiary);
	}

	@Override
	public Beneficiary getBeneficiaryByIdAndCustomer(final String mobileNo, final Integer beneficiaryId)
			throws CustomerException, BeneficiaryException {

		val customer = this.customerDAO.findByMobileNo(mobileNo);
		if (customer == null) {
			throw new CustomerException("Customer not found");
		}

		val beneficiary = this.beneficiaryDAO.findById(beneficiaryId)
				.orElseThrow(() -> new BeneficiaryException("Beneficiary not found"));

		if (!beneficiary.getCustomer().equals(customer)) {
			throw new BeneficiaryException("Beneficiary does not belong to the current user");
		}

		return beneficiary;
	}
}
