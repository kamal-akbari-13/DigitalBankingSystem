package com.paykaro.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillPayment {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer billId;

	private String billType;
	private Double amount;
	private LocalDate paymentDate = LocalDate.now(); // Set default directly

	@ManyToOne
	@JoinColumn(name = "wallet_id")
	private Wallet wallet;
}
