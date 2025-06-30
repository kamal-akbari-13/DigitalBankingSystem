package com.paykaro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistoryDTO {

    private Integer id;
    private Double amount;
    private String type; // "CREDIT" or "DEBIT"
    private String description;
    private LocalDateTime timestamp;

    // Only included for DEBIT transactions
    private String sourceType;
    private Integer sourceId;
    private String targetType;
    private Integer targetId;

    // Optional sender information (for CREDIT)
    private String senderMobileNo;
    private String senderAccountId;
}
