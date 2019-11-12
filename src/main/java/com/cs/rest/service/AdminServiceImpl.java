package com.cs.rest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.cs.entity.Company;
import com.cs.entity.Coupon;
import com.cs.entity.Customer;
import com.cs.repository.CompanyRepository;
import com.cs.repository.CouponRepository;
import com.cs.repository.CustomerRepository;

@Service
@Scope("prototype")
public class AdminServiceImpl extends AbsService implements AdminService {

	private CompanyRepository companyRepository;
	private CouponRepository couponRepository;
	private CustomerRepository customerRepository;

	@Autowired
	public AdminServiceImpl(CompanyRepository companyRepository, CouponRepository couponRepository,
			CustomerRepository customerRepository) {
		this.companyRepository = companyRepository;
		this.couponRepository = couponRepository;
		this.customerRepository = customerRepository;
	}

	@Override
	public Customer saveCustomer(Customer customer) {
		return customerRepository.save(customer);
	}

	@Override
	public void deleteCustomer(long customerId) {
		customerRepository.deleteById(customerId);
	}

	@Override
	public List<Customer> findAllCustomers() {
		return customerRepository.findAll();
	}

	@Override
	public Customer findCustomerById(long customerId) {
		return customerRepository.findById(customerId).orElse(null);
	}

	@Override
	public List<Coupon> findCustomerCoupons(long id) {
		return couponRepository.findAllCouponsByCustomerId(id);
	}

	@Override
	public Company saveCompany(Company company) {
		return companyRepository.save(company);
	}

	@Override
	public void deleteCompany(long companyId) {
		companyRepository.deleteById(companyId);
	}

	@Override
	public List<Company> findAllCompanies() {
		return companyRepository.findAll();
	}

	@Override
	public Company findCompanyById(long companyId) {
		return companyRepository.findById(companyId).orElse(null);
	}

	@Override
	public List<Coupon> findCompanyCoupons(long id) {
		return couponRepository.findAllCouponsByCompanyId(id);
	}

	@Override
	public Coupon saveCoupon(Coupon coupon, long companyId) {
		Optional<Company> company = companyRepository.findById(companyId);

		if (company.isPresent()) {
			coupon.setCompany(company.get());
			couponRepository.save(coupon);
		}
		return null;
	}

	@Override
	public void deleteCoupon(long couponId) {
		couponRepository.deleteById(couponId);
	}

	@Override
	public List<Coupon> findAllCoupons() {
		return couponRepository.findAll();
	}

	@Override
	public Coupon findCouponById(long couponId) {
		return couponRepository.findById(couponId).orElse(null);
	}

	public long getCompanyIdFromCoupon(long couponId) {
		Optional<Coupon> coupon = couponRepository.findById(couponId);

		if (coupon.isPresent()) {
			return coupon.get().getCompany().getId();
		}

		return 0;
	}

}
