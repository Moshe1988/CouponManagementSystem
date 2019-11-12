package com.cs.rest;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.cs.entity.Company;
import com.cs.entity.Customer;
import com.cs.repository.CompanyRepository;
import com.cs.repository.CustomerRepository;
import com.cs.rest.ex.InvalidLoginException;
import com.cs.rest.service.AdminServiceImpl;
import com.cs.rest.service.CompanyServiceImpl;
import com.cs.rest.service.CustomerServiceImpl;

@Service
public class CouponSystem {

	private ApplicationContext context;
	private CustomerRepository customerRepository;
	private CompanyRepository companyRepository;

	private CouponCleaner couponCleaner;
	private SessionCleaner sessionCleaner;

	@Autowired
	public CouponSystem(ApplicationContext context, CustomerRepository customerRepository,
			CompanyRepository companyRepository) {
		this.context = context;
		this.customerRepository = customerRepository;
		this.companyRepository = companyRepository;
	}

	/**
	 * This method is the main login method and it will send the correct login to
	 * the right method below by the type given.
	 * 
	 * @param email    - the email of the user that want to login.
	 * @param password - the password of the user that want to login.
	 * @param type     - the type of the user that want to login.
	 * @return 'ClientSession' contains the correct service.
	 * @throws InvalidLoginException - in case the email, password or type are not a
	 *                               match.
	 */
	public ClientSession login(String email, String password, String type) throws InvalidLoginException {
		switch (type) {
			case "admin":
				return adminLogin(email, password);
			case "company":
				return companyLogin(email, password);
			case "customer":
				return customerLogin(email, password);
			default:
				throw new InvalidLoginException("The email or password are invalid!");
		}
	}

	@Autowired
	public void setCouponCleaner(CouponCleaner couponCleaner) {
		this.couponCleaner = couponCleaner;
	}

	@Autowired
	public void setSessionCleaner(SessionCleaner sessionCleaner) {
		this.sessionCleaner = sessionCleaner;
	}

	/**
	 * Activate the 'CouponCleaner' to delete expired coupons and the
	 * 'SessionCleaner' to delete session after some time that .
	 */
	@PostConstruct
	public void onPostConstruct() {
		new Thread(couponCleaner).start();
		new Thread(sessionCleaner).start();
	}

	/**
	 * Shutdown the 'CouponCleaner' and 'SessionCleaner' if active.
	 */
	@PreDestroy
	public void onPreDestroy() {
		couponCleaner.stop();
		sessionCleaner.stop();
	}

	/**
	 * This method will check if the system contains customer with the email and
	 * password given and return a 'ClientSession' contains service with the id of
	 * the specific customer that matches those.
	 * 
	 * @param email    - the email of the customer that want to login.
	 * @param password - the password of the customer that want to login.
	 * @return 'ClientSession' contains the customer service
	 *         (see @CustomerServiceImpl in setCustomerId()).
	 * @throws InvalidLoginException- in case the email, password or type are not a
	 *                                match.
	 */
	private ClientSession customerLogin(String email, String password) throws InvalidLoginException {
		Customer customer = customerRepository.findCustomerByEmailAndPassword(email, password);

		if (customer == null) {
			throw new InvalidLoginException("The email or password are invalid!");
		}

		CustomerServiceImpl servise = context.getBean(CustomerServiceImpl.class);
		servise.setCustomerId(customer.getId());
		ClientSession session = context.getBean(ClientSession.class);
		session.setService(servise);
		session.accessed();
		return session;
	}

	/**
	 * This method will check if the system contains company with the email and
	 * password given and return a 'ClientSession' contains service with the id of
	 * the specific company that matches those.
	 * 
	 * @param email    - the email of the company that want to login.
	 * @param password - the password of the company that want to login.
	 * @return 'ClientSession' contains the customer service
	 *         (see @CompanyServiceImpl in setCompanyId()).
	 * @throws InvalidLoginException- in case the email, password or type are not a
	 *                                match.
	 */
	private ClientSession companyLogin(String email, String password) throws InvalidLoginException {
		Company company = companyRepository.findCompanyByEmailAndPassword(email, password);

		if (company == null) {
			throw new InvalidLoginException("The email or password are invalid!");
		}

		CompanyServiceImpl servise = context.getBean(CompanyServiceImpl.class);
		servise.setCompanyId(company.getId());
		ClientSession session = context.getBean(ClientSession.class);
		session.setService(servise);
		session.accessed();
		return session;
	}

	/**
	 * This method will check if the admin in the application is match with the
	 * email and password given and return a 'ClientSession' contains admin service.
	 * 
	 * @param email    - the email of the admin that want to login.
	 * @param password - the password of the admin that want to login.
	 * @return 'ClientSession' contains the admin service (see @AdminServiceImpl).
	 * @throws InvalidLoginException- in case the email, password or type are not a
	 *                                match.
	 */
	private ClientSession adminLogin(String email, String password) throws InvalidLoginException {
		if ("admin".equals(email) && "1234".equals(password)) {
			AdminServiceImpl service = context.getBean(AdminServiceImpl.class);
			ClientSession session = context.getBean(ClientSession.class);
			session.setService(service);
			session.accessed();
			return session;
		}
		throw new InvalidLoginException("The email or password are invalid!");
	}
}
