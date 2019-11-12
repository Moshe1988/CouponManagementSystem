package com.cs.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cs.entity.Coupon;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

	List<Coupon> findAllCouponsByCompanyId(long companyId);

	@Query("SELECT c FROM Customer cust JOIN cust.coupons c WHERE cust.id = :customerId")
	List<Coupon> findAllCouponsByCustomerId(long customerId);

	List<Coupon> findAllByEndDateBefore(Date date);
}
