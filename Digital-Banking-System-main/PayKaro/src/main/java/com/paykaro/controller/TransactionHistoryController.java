package com.paykaro.controller;

import com.paykaro.dto.TransactionHistoryDTO;
import com.paykaro.exception.CustomerException;
import com.paykaro.security.JwtUtil;
import com.paykaro.service.TransactionHistoryService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/customers/transactions")
public class TransactionHistoryController {

    @Autowired
    private TransactionHistoryService transactionHistoryService;

    @Autowired
    private JwtUtil jwtUtil;

    private String extractMobileNoFromToken(final HttpServletRequest request) {
        val authorizationHeader = request.getHeader("Authorization");
        val token = authorizationHeader.substring(7);
        return jwtUtil.extractUsername(token);
    }

    @GetMapping
    public ResponseEntity<List<TransactionHistoryDTO>> getAllTransactions(final HttpServletRequest request)
            throws CustomerException {
        val mobileNo = extractMobileNoFromToken(request);
        val transactions = transactionHistoryService.getAllTransactionsForCustomer(mobileNo);
        return ResponseEntity.ok(transactions);
    }
}
