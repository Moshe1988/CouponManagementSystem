package com.cs.rest.service;

import java.util.List;

import com.cs.entity.Company;
import com.cs.entity.Coupon;
import com.cs.entity.Customer;

public interface AdminService {

	Customer saveCustomer(Customer customer);

	void deleteCustomer(long customerId);

	List<Customer> findAllCustomers();

	Customer findCustomerById(long customerId);

	List<Coupon> findCustomerCoupons(long customerId);

	Company saveCompany(Company company);

	void deleteCompany(long companyId);

	List<Company> findAllCompanies();

	Company findCompanyById(long companyId);

	List<Coupon> findCompanyCoupons(long companyId);

	Coupon saveCoupon(Coupon coupon, long companyId);

	void deleteCoupon(long couponId);

	List<Coupon> findAllCoupons();

	Coupon findCouponById(long couponId);

	/**
	 * That function will return the id of the company that associated to that
	 * coupon by the coupon id. For use in client side UX.
	 * 
	 * @param couponId - the id of the coupon we want to get the company id of.
	 * @return id - the company id that created that coupon.
	 */
	long getCompanyIdFromCoupon(long couponId);

}
