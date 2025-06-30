package com.paykaro.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer accId;

	private String accountNo;
	private String ifscCode;
	private String bankName;
	private Double balance;

	@ManyToOne
	@JoinColumn(name = "wallet_id")
	@JsonBackReference
	private Wallet wallet;
}
