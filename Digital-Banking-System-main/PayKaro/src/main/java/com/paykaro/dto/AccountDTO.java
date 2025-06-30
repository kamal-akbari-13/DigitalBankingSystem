package com.paykaro.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDTO {

    private String accountNo;
    private String ifscCode;
    private String bankName;
    private Double balance;

    // Lombok @Data already provides getters and setters, no need to manually define them
}
