package com.paykaro.controller;

import com.paykaro.dto.BeneficiaryDTO;
import com.paykaro.exception.AccountException;
import com.paykaro.exception.BeneficiaryException;
import com.paykaro.exception.CustomerException;
import com.paykaro.model.Beneficiary;
import com.paykaro.security.JwtUtil;
import com.paykaro.service.BeneficiaryService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/customers/beneficiaries")
public class BeneficiaryController {

	@Autowired
	private BeneficiaryService beneficiaryService;

	@Autowired
	private JwtUtil jwtUtil;

	private String extractMobileNoFromToken(final HttpServletRequest request) {
		val authorizationHeader = request.getHeader("Authorization");
		val token = authorizationHeader.substring(7);
		return jwtUtil.extractUsername(token);
	}

	//  Add a new beneficiary
	@PostMapping
	public ResponseEntity<Beneficiary> addBeneficiary(
			@RequestBody final BeneficiaryDTO beneficiaryDTO,
			final HttpServletRequest request
	) throws CustomerException, AccountException {

		val mobileNo = extractMobileNoFromToken(request);
		val saved = beneficiaryService.addBeneficiary(mobileNo, beneficiaryDTO);
		return ResponseEntity.ok(saved);
	}

	//  Get all beneficiaries for logged-in customer
	@GetMapping
	public ResponseEntity<List<Beneficiary>> getBeneficiaries(
			final HttpServletRequest request
	) throws CustomerException {

		val mobileNo = extractMobileNoFromToken(request);
		val list = beneficiaryService.getBeneficiaries(mobileNo);
		return ResponseEntity.ok(list);
	}

	//  Delete beneficiary by ID
	@DeleteMapping("/{beneficiaryId}")
	public ResponseEntity<String> deleteBeneficiary(
			@PathVariable final Integer beneficiaryId,
			final HttpServletRequest request
	) throws CustomerException, BeneficiaryException {

		val mobileNo = extractMobileNoFromToken(request);
		beneficiaryService.deleteBeneficiary(mobileNo, beneficiaryId);
		return ResponseEntity.ok("Beneficiary deleted successfully");
	}
}
