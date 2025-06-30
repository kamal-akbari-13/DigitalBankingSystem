package com.paykaro.controller;

import com.paykaro.exception.BillPaymentException;
import com.paykaro.exception.CustomerException;
import com.paykaro.exception.WalletException;
import com.paykaro.model.BillPayment;
import com.paykaro.security.JwtUtil;
import com.paykaro.service.BillPaymentService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/payments")
public class BillPaymentController {

	@Autowired
	private BillPaymentService billPaymentService;

	@Autowired
	private JwtUtil jwtUtil;

	private String extractMobileNoFromToken(final HttpServletRequest request) {
		val authorizationHeader = request.getHeader("Authorization");
		val token = authorizationHeader.substring(7);
		return jwtUtil.extractUsername(token);
	}

	// Add a new bill payment
	@PostMapping("/bills")
	public ResponseEntity<BillPayment> addBillPaymentHandler(
			@RequestBody final BillPayment billPayment,
			final HttpServletRequest request
	) throws BillPaymentException, CustomerException, WalletException {

		val mobileNo = extractMobileNoFromToken(request);
		val savedBillPayment = billPaymentService.addBillPayment(billPayment, mobileNo);
		return new ResponseEntity<>(savedBillPayment, HttpStatus.CREATED);
	}

	// View all bill payments for the logged-in customer
	@GetMapping("/bills")
	public ResponseEntity<List<BillPayment>> viewAllBillPaymentsHandler(
			final HttpServletRequest request
	) throws CustomerException, WalletException, BillPaymentException {

		val mobileNo = extractMobileNoFromToken(request);
		val billPayments = billPaymentService.viewAllBillPayments(mobileNo);
		return new ResponseEntity<>(billPayments, HttpStatus.OK);
	}
}
