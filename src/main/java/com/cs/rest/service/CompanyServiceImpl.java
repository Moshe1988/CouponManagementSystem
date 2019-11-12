package com.cs.rest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.cs.entity.Company;
import com.cs.entity.Coupon;
import com.cs.repository.CompanyRepository;
import com.cs.repository.CouponRepository;

@Service
@Scope("prototype")
public class CompanyServiceImpl extends AbsService implements CompanyService {

	private long companyId;

	private CompanyRepository companyRepository;
	private CouponRepository couponRepository;

	@Autowired
	public CompanyServiceImpl(CompanyRepository companyRepository, CouponRepository couponRepository,
			ApplicationContext context) {
		this.companyRepository = companyRepository;
		this.couponRepository = couponRepository;
	}

	@Override
	public Company save(Company company) {
		return companyRepository.save(company);
	}

	@Override
	public Company findCurrentCompany() {
		Optional<Company> company = companyRepository.findById(this.companyId);
		return company.orElse(null);
	}

	@Override
	public Coupon save(Coupon coupon) {
		Optional<Company> company = companyRepository.findById(this.companyId);

		if (company.isPresent()) {
			coupon.setCompany(company.get());
			return couponRepository.save(coupon);
		}
		return null;
	}

	@Override
	public List<Coupon> findCompanyCoupons() {
		return couponRepository.findAllCouponsByCompanyId(this.companyId);
	}

	@Override
	public Coupon findCouponById(long couponId) {
		Optional<Coupon> coupon = couponRepository.findById(couponId);
		return coupon.orElse(null);
	}

	@Override
	public List<Coupon> findAllCoupons() {
		return couponRepository.findAll();
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

}
