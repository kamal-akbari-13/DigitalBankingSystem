package com.paykaro.service;

import com.paykaro.dto.TransactionHistoryDTO;
import com.paykaro.exception.CustomerException;
import com.paykaro.model.Customer;
import com.paykaro.model.TransactionHistory;
import com.paykaro.repository.CustomerDAO;
import com.paykaro.repository.TransactionHistoryRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionHistoryServiceImpl implements TransactionHistoryService {

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    private CustomerDAO customerDAO;

    @Override
    public void recordTransaction(
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
    ) {
        val transaction = new TransactionHistory();
        transaction.setCustomer(customer);
        transaction.setType(type);
        transaction.setSourceType(sourceType);
        transaction.setSourceId(sourceId);
        transaction.setTargetType(targetType);
        transaction.setTargetId(targetId);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setSenderMobileNo(senderMobileNo);
        transaction.setSenderAccountId(senderAccountId);
        transaction.setTimestamp(LocalDateTime.now());

        transactionHistoryRepository.save(transaction);
    }

    @Override
    public List<TransactionHistoryDTO> getAllTransactionsForCustomer(final String mobileNo) throws CustomerException {
        final Customer customer = customerDAO.findByMobileNo(mobileNo);
        if (customer == null) {
            throw new CustomerException("Customer not found");
        }

        val transactions = transactionHistoryRepository.findByCustomer(customer);
        return transactions.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // This method is private since it's only used within this class
    private TransactionHistoryDTO convertToDTO(final TransactionHistory transaction) {
        val dto = new TransactionHistoryDTO();
        dto.setId(transaction.getId());
        dto.setAmount(transaction.getAmount());
        dto.setType(transaction.getType());
        dto.setDescription(transaction.getDescription());
        dto.setTimestamp(transaction.getTimestamp());

        // Only include source/target info for DEBIT type
        if ("DEBIT".equalsIgnoreCase(transaction.getType())) {
            dto.setSourceType(transaction.getSourceType());
            dto.setSourceId(transaction.getSourceId());
            dto.setTargetType(transaction.getTargetType());
            dto.setTargetId(transaction.getTargetId());
        }
        else if ("CREDIT".equalsIgnoreCase(transaction.getType())) {
            dto.setSourceType(transaction.getTargetType());
            dto.setSourceId(transaction.getTargetId());
            dto.setTargetType(transaction.getSourceType());
            dto.setTargetId(transaction.getSourceId());
        }
        // Always include sender info if present
        dto.setSenderMobileNo(transaction.getSenderMobileNo());
        dto.setSenderAccountId(transaction.getSenderAccountId());

        return dto;
    }
}
