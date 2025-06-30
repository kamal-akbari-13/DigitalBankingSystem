package com.paykaro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paykaro.exception.CustomerException;
import com.paykaro.model.Customer;
import com.paykaro.repository.CustomerDAO;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerDAO customerDAO;

	@Transactional
	@Override
	public Customer createCustomer(final Customer customer) throws CustomerException {
		final Customer existingCustomer = this.customerDAO.findByMobileNo(customer.getMobileNo());
		if (existingCustomer != null) {
			throw new CustomerException("Customer already registered with this mobile number");
		}

		return this.customerDAO.save(customer);
	}

	@Transactional
	@Override
	public Customer updateCustomer(final Customer customer, final String mobileNo) throws CustomerException {
		final Customer existingCustomer = this.customerDAO.findByMobileNo(mobileNo);
		if (existingCustomer == null) {
			throw new CustomerException("Customer not found");
		}

		if (!customer.getCid().equals(existingCustomer.getCid())) {
			throw new CustomerException("You are not authorized to update this customer");
		}

		customer.setMobileNo(existingCustomer.getMobileNo());
		customer.setPassword(existingCustomer.getPassword());

		return this.customerDAO.save(customer);
	}

	@Override
	public Customer getCustomerByMobileNo(final String mobileNo) throws CustomerException {
		final Customer customer = this.customerDAO.findByMobileNo(mobileNo);
		if (customer == null) {
			throw new CustomerException("Customer not found");
		}
		return customer;
	}
}
