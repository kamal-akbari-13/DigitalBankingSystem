package com.paykaro.security;

import com.paykaro.model.Customer;
import com.paykaro.repository.CustomerDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerDAO customerDAO;

    @Override
    public UserDetails loadUserByUsername(String mobileNo) throws UsernameNotFoundException {
        Customer customer = customerDAO.findByMobileNo(mobileNo);
        if (customer == null) {
            throw new UsernameNotFoundException("User not found with mobile no: " + mobileNo);
        }

        return new CustomUserDetails(customer);
    }
}
