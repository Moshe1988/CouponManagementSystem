package com.cs.rest;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cs.entity.Coupon;
import com.cs.repository.CouponRepository;

@Service
public class CouponCleaner implements Runnable {

	private static final long DAY_LENGTH_MILLIS = 86_400_000;

	private boolean isWorking;
	private long lastCleanedMillis = 0;
	private CouponRepository couponRepository;

	@Autowired
	public CouponCleaner(CouponRepository couponRepository) {
		this.couponRepository = couponRepository;
	}

	/**
	 * This function will run every day and get all the coupons that the 'endDate'
	 * of them is expired, and delete if it finds such coupons.
	 */
	@Override
	public void run() {

		isWorking = true;

		while (isWorking) {

			if (System.currentTimeMillis() - lastCleanedMillis >= DAY_LENGTH_MILLIS) {

				lastCleanedMillis = System.currentTimeMillis();

				List<Coupon> coupons = couponRepository.findAllByEndDateBefore(new Date());

				if (!coupons.isEmpty()) {
					couponRepository.deleteAll(coupons);
				}
			}
		}

	}

	public void stop() {
		isWorking = false;
	}

}
