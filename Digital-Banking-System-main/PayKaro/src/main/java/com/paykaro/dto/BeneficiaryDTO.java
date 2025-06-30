package com.paykaro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaryDTO {

    private String beneficiaryName;
    private String targetCustomerMobile;
    private Integer targetAccountId;
}
