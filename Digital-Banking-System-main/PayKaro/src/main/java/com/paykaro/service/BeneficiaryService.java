package com.paykaro.service;

import java.util.List;

import com.paykaro.dto.BeneficiaryDTO;
import com.paykaro.exception.AccountException;
import com.paykaro.exception.BeneficiaryException;
import com.paykaro.exception.CustomerException;
import com.paykaro.model.Beneficiary;

public interface BeneficiaryService {

	// Add beneficiary using DTO (safe validated)
	Beneficiary addBeneficiary(final String mobileNo, final BeneficiaryDTO beneficiaryDTO)
			throws CustomerException, AccountException;

	List<Beneficiary> getBeneficiaries(final String mobileNo) throws CustomerException;

	void deleteBeneficiary(final String mobileNo, final Integer beneficiaryId)
			throws CustomerException, BeneficiaryException;

	Beneficiary getBeneficiaryByIdAndCustomer(final String mobileNo, final Integer beneficiaryId)
			throws CustomerException, BeneficiaryException;
}
