package com.cs.rest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.cs.entity.Coupon;
import com.cs.entity.Customer;
import com.cs.repository.CouponRepository;
import com.cs.repository.CustomerRepository;

@Service
@Scope("prototype")
public class CustomerServiceImpl extends AbsService implements CustomerService {

	private long customerId;

	private CustomerRepository customerRepository;
	private CouponRepository couponRepository;

	@Autowired
	public CustomerServiceImpl(CustomerRepository customerRepository, CouponRepository couponRepository) {
		this.customerRepository = customerRepository;
		this.couponRepository = couponRepository;
	}

	@Override
	public Customer save(Customer customer) {
		return customerRepository.save(customer);
	}

	@Override
	public Customer findCurrentCustomer() {
		Optional<Customer> customer = customerRepository.findById(this.customerId);
		return customer.orElse(null);
	}

	@Override
	public List<Coupon> findCustomerCoupons() {
		return couponRepository.findAllCouponsByCustomerId(this.customerId);
	}

	@Override
	public Coupon purchaseCoupon(long couponId) {
		
		Optional<Customer> customerOp = customerRepository.findById(this.customerId);
		
		Optional<Coupon> couponOp = couponRepository.findById(couponId);

		if (customerOp.isPresent() && couponOp.isPresent()) {
			Coupon coupon = couponOp.get();
			Customer customer = customerOp.get();

			customer.addCoupon(coupon);
			coupon.setAmount(coupon.getAmount() - 1);
			customerRepository.save(customer);
			return coupon;
		}
		return null;
	}

	@Override
	public List<Coupon> findAllCoupons() {
		return couponRepository.findAll();
	}

	@Override
	public Coupon findCouponById(long couponId) {
		Optional<Coupon> coupon = couponRepository.findById(couponId);
		return coupon.orElse(null);
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

}
