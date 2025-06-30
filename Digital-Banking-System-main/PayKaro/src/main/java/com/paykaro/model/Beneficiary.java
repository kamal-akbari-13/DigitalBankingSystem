package com.paykaro.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Beneficiary {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer beneficiaryId;

	private String beneficiaryName;
	private String targetCustomerMobile;
	private Integer targetAccountId;

	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;
}
