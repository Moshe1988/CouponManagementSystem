package com.cs.rest.service;

import java.util.List;

import com.cs.entity.Coupon;
import com.cs.entity.Customer;

public interface CustomerService {

	Customer save(Customer customer);

	Customer findCurrentCustomer();

	List<Coupon> findCustomerCoupons();

	Coupon purchaseCoupon(long couponId);

	List<Coupon> findAllCoupons();

	/**
	 * This function is use to set the customer id in order to make the service
	 * available only to the correct customer that return from the login.
	 * see(@CouponSystem in 'customerLogin' function).
	 * 
	 * @param id - the id customer to set in the service.
	 */
	Coupon findCouponById(long couponId);

}
