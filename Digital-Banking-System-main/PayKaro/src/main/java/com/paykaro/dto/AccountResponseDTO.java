package com.paykaro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDTO {

    private Integer accId;
    private String accountNo;
    private String ifscCode;
    private String bankName;
    private Double balance;
}
