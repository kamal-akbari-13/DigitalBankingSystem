package com.paykaro.service;

import com.paykaro.exception.CustomerException;
import com.paykaro.model.Customer;

public interface CustomerService {

	Customer createCustomer(final Customer customer) throws CustomerException;

	Customer updateCustomer(final Customer customer, final String mobileNo) throws CustomerException;

	Customer getCustomerByMobileNo(final String mobileNo) throws CustomerException;
}
