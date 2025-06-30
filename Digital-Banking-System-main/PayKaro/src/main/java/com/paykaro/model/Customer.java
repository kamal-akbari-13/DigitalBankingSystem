package com.paykaro.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@Data
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer cid;

	@NotBlank
	private String name;

	@NotBlank
	@Column(unique = true)
	private String mobileNo;

	@NotBlank
	private String password;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "wallet_id", referencedColumnName = "wid")
	private Wallet wallet;

	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<Beneficiary> beneficiaries;

	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<TransactionHistory> transactions;
}
