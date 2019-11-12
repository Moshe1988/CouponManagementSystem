package com.cs.rest.service;

import java.util.List;

import com.cs.entity.Company;
import com.cs.entity.Coupon;

public interface CompanyService {

	Company save(Company company);

	Company findCurrentCompany();

	Coupon save(Coupon coupon);

	List<Coupon> findCompanyCoupons();

	List<Coupon> findAllCoupons();

	Coupon findCouponById(long couponId);

	/**
	 * This function is use to set the company id in order to make the service
	 * available only to the correct company that return from the login. see
	 * 'CouponSystem' in 'companyLogin' function.
	 * 
	 * @param id - the id company to set in the service.
	 */
	void setCompanyId(long id);

}
