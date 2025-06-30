package com.paykaro.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Double amount;
    private String type;  // CREDIT or DEBIT
    private String sourceType;  // WALLET or ACCOUNT
    private Integer sourceId;
    private String targetType;  // WALLET or ACCOUNT
    private Integer targetId;
    private String description;

    private String senderMobileNo;
    private String senderAccountId;

    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
