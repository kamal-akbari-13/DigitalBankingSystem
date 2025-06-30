package com.paykaro.service;

import com.paykaro.dto.TransactionHistoryDTO;
import com.paykaro.exception.CustomerException;
import com.paykaro.model.Customer;

import java.util.List;

public interface TransactionHistoryService {

    void recordTransaction(
            final Customer customer,
            final String type,
            final String sourceType,
            final Integer sourceId,
            final String targetType,
            final Integer targetId,
            final Double amount,
            final String description,
            final String senderMobileNo,
            final String senderAccountId
    );

    List<TransactionHistoryDTO> getAllTransactionsForCustomer(final String mobileNo) throws CustomerException;
}
