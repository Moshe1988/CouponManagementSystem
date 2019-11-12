package com.cs.rest.controller;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cs.entity.Company;
import com.cs.entity.Coupon;
import com.cs.entity.Customer;
import com.cs.rest.ClientSession;
import com.cs.rest.ex.EmailAlreadyExistsException;
import com.cs.rest.ex.IllegalChangeException;
import com.cs.rest.ex.IllegalCouponException;
import com.cs.rest.ex.InvalidLoginException;
import com.cs.rest.ex.InvalidUserException;
import com.cs.rest.ex.NoCompanyFoundException;
import com.cs.rest.ex.NoCouponFoundException;
import com.cs.rest.ex.NoCustomerFoundException;
import com.cs.rest.service.AdminService;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/api")
public class AdminController {

	/**
	 * This map defined in 'RestConfiguration' in order to to access the same map
	 * from everywhere in the application.
	 */
	private Map<String, ClientSession> tokensMap;

	@Autowired
	public AdminController(@Qualifier("tokens") Map<String, ClientSession> tokensMap) {
		this.tokensMap = tokensMap;
	}

	/**
	 * This function is getting the service of the admin that stored in the
	 * 'tokensMap' inside the 'ClientSession' by the key 'token' given, or throws
	 * exception if the session expired or the token not exist. the function also
	 * notify the session that it been accessed.
	 * 
	 * @param token - the key of the value 'ClientSession' that stored in
	 *              'tokensMap'.
	 * @return AdminService - the service of the admin that matches the token that
	 *         was given.
	 * @throws InvalidLoginException - if the token given not match to any
	 *                               'ClientSession' in the 'tokensMap' or the
	 *                               session was expired.
	 */
	private AdminService getService(String token)
			throws InvalidLoginException {
		ClientSession clientSession = tokensMap.get(token);

		if (clientSession == null) {
			throw new InvalidLoginException("The login timed out, please login again.");
		}

		clientSession.accessed();

		return (AdminService) clientSession.getService();
	}

	// ------------------------------------------Customer--------------------------------------------//

	@PostMapping("/admin/customers/{token}")
	public ResponseEntity<Customer> addCustomer(@RequestBody Customer customer, @PathVariable String token)
			throws InvalidLoginException, EmailAlreadyExistsException, InvalidUserException {

		checkIfCustomerIsValid(customer, token);

		checkIfCustomerEmailExists(customer, token);

		customer.setId(0);
		getService(token).saveCustomer(customer);
		return ResponseEntity.ok(customer);
	}

	@PutMapping("/admin/customers/{token}")
	public ResponseEntity<Customer> updateCustomer(@RequestBody Customer customer, @PathVariable String token)
			throws InvalidLoginException, IllegalChangeException, NoCustomerFoundException, InvalidUserException {

		checkIfCustomerIsValid(customer, token);

		checkIfCustomerEmailChanged(customer, token);

		getService(token).saveCustomer(customer);
		return ResponseEntity.ok(customer);
	}

	@DeleteMapping("/admin/customers/{id}/{token}")
	public void deleteCustomer(@PathVariable long id, @PathVariable String token)
			throws InvalidLoginException, NoCustomerFoundException {

		checkIfCustomerExists(id, token);

		getService(token).deleteCustomer(id);
	}

	@GetMapping("/admin/customers/{token}")
	public ResponseEntity<Collection<Customer>> findAllCustomers(@PathVariable String token)
			throws InvalidLoginException {

		List<Customer> allCustomers = getService(token).findAllCustomers();

		if (allCustomers.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(allCustomers);
	}

	@GetMapping("/admin/customers/{id}/{token}")
	public ResponseEntity<Customer> findCustomerById(@PathVariable long id, @PathVariable String token)
			throws InvalidLoginException, NoCustomerFoundException {

		checkIfCustomerExists(id, token);

		Customer customer = getService(token).findCustomerById(id);

		return ResponseEntity.ok(customer);
	}

	@GetMapping("/admin/customers/{id}/coupons/{token}")
	public ResponseEntity<Collection<Coupon>> findCustomerCoupons(@PathVariable long id, @PathVariable String token)
			throws InvalidLoginException, NoCustomerFoundException {

		checkIfCustomerExists(id, token);

		List<Coupon> customerCoupons = getService(token).findCustomerCoupons(id);

		if (customerCoupons.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(customerCoupons);
	}

	// ------------------------------------------Company--------------------------------------------//

	@PostMapping("/admin/companies/{token}")
	public ResponseEntity<Company> addCompany(@RequestBody Company company, @PathVariable String token)
			throws InvalidLoginException, EmailAlreadyExistsException, InvalidUserException {

		checkIfCompanyIsValid(company, token);

		checkIfCompanyEmailExists(company, token);

		company.setId(0);
		getService(token).saveCompany(company);
		return ResponseEntity.ok(company);
	}

	@PutMapping("/admin/companies/{token}")
	public ResponseEntity<Company> updateCompany(@RequestBody Company company, @PathVariable String token)
			throws InvalidLoginException, IllegalChangeException, NoCompanyFoundException, InvalidUserException {

		checkIfCompanyIsValid(company, token);

		checkIfCompanyEmailChanged(company, token);

		getService(token).saveCompany(company);
		return ResponseEntity.ok(company);
	}

	@DeleteMapping("/admin/companies/{id}/{token}")
	public void deleteCompany(@PathVariable long id, @PathVariable String token)
			throws InvalidLoginException, NoCompanyFoundException {

		checkIfCompanyExists(id, token);

		getService(token).deleteCompany(id);
	}

	@GetMapping("/admin/companies/{token}")
	public ResponseEntity<Collection<Company>> findAllCompanies(@PathVariable String token)
			throws InvalidLoginException {

		List<Company> allCompanies = getService(token).findAllCompanies();

		if (allCompanies.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(allCompanies);
	}

	@GetMapping("/admin/companies/{id}/{token}")
	public ResponseEntity<Company> findCompanyById(@PathVariable long id, @PathVariable String token)
			throws InvalidLoginException, NoCompanyFoundException {

		checkIfCompanyExists(id, token);

		Company company = getService(token).findCompanyById(id);

		return ResponseEntity.ok(company);
	}

	@GetMapping("/admin/companies/{id}/coupons/{token}")
	public ResponseEntity<Collection<Coupon>> findCompanyCoupons(@PathVariable long id, @PathVariable String token)
			throws InvalidLoginException, NoCompanyFoundException {

		checkIfCompanyExists(id, token);

		List<Coupon> companyCoupons = getService(token).findCompanyCoupons(id);

		if (companyCoupons.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(companyCoupons);
	}

	// ------------------------------------------Coupon--------------------------------------------//

	@PostMapping("/admin/companies/{id}/coupons/{token}")
	public ResponseEntity<Coupon> addCoupon(@RequestBody Coupon coupon, @PathVariable long id,
			@PathVariable String token) throws InvalidLoginException, NoCompanyFoundException, IllegalCouponException {

		checkIfCouponIsValid(coupon, token);

		checkIfCompanyExists(id, token);

		checkIfCouponTitleExists(coupon, token);

		coupon.setId(0);
		getService(token).saveCoupon(coupon, id);
		return ResponseEntity.ok(coupon);
	}

	@PutMapping("/admin/companies/{id}/coupons/{token}")
	public ResponseEntity<Coupon> updateCoupon(@RequestBody Coupon coupon, @PathVariable long id,
			@PathVariable String token)
			throws InvalidLoginException, NoCompanyFoundException, IllegalCouponException, NoCouponFoundException,
			IllegalChangeException {

		checkIfCouponValidToUpdate(coupon, id, token);

		getService(token).saveCoupon(coupon, id);
		return ResponseEntity.ok(coupon);
	}

	@DeleteMapping("/admin/coupons/{id}/{token}")
	public void deleteCoupon(@PathVariable long id, @PathVariable String token)
			throws InvalidLoginException, NoCouponFoundException {

		checkIfCouponExists(id, token);

		getService(token).deleteCoupon(id);
	}

	@GetMapping("/admin/coupons/{token}")
	public ResponseEntity<Collection<Coupon>> findAllCoupons(@PathVariable String token)
			throws InvalidLoginException {

		List<Coupon> allCoupons = getService(token).findAllCoupons();

		if (allCoupons.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(allCoupons);
	}

	@GetMapping("/admin/coupons/{id}/{token}")
	public ResponseEntity<Coupon> findCouponById(@PathVariable long id, @PathVariable String token)
			throws InvalidLoginException, NoCouponFoundException {

		checkIfCouponExists(id, token);

		Coupon coupon = getService(token).findCouponById(id);

		return ResponseEntity.ok(coupon);
	}

	/**
	 * That function will return the id of the company that associated to that
	 * coupon by the coupon id. For use in client side UX.
	 * 
	 * @param id    - the id of the coupon we want to get the company id of.
	 * @param token - the token key to get the service from.
	 * @return the company id that listed to that coupon.
	 * @throws InvalidLoginException   - if the token is invalid or expired.
	 * @throws NoCompanyFoundException - if the function not able to get any company
	 *                                 from that id.
	 * @throws NoCouponFoundException
	 */
	@GetMapping("/admin/coupons/companyId/{id}/{token}")
	public ResponseEntity<Long> GetCompanyIdFromCoupon(@PathVariable long id, @PathVariable String token)
			throws InvalidLoginException, NoCompanyFoundException, NoCouponFoundException {

		checkIfCouponExists(id, token);

		long companyId = getService(token).getCompanyIdFromCoupon(id);

		checkIfCompanyExists(companyId, token);

		return ResponseEntity.ok(companyId);
	}

	// -----------------------------------Customer-Utils--------------------------------------------//

	/**
	 * This is a check method to verify that will be no email duplicates in the
	 * system.
	 * 
	 * @param customer - the customer to check.
	 * @param token    - the token of the admin that ask for the check.
	 * @throws InvalidLoginException       - in case that the token is invalid or
	 *                                     expired.
	 * @throws EmailAlreadyExistsException - in case the email already exists in the
	 *                                     system.
	 */
	private void checkIfCustomerEmailExists(Customer customer, String token)
			throws InvalidLoginException, EmailAlreadyExistsException {

		List<Customer> allCustomers = getService(token).findAllCustomers();

		for (Customer cust : allCustomers) {
			if (customer.getEmail().equals(cust.getEmail())) {
				throw new EmailAlreadyExistsException(
						String.format("Unable to create new customer, the email '%s' already exists in the system.",
								customer.getEmail()));
			}
		}
	}

	/**
	 * This is a check method that checks if customer have email and password.
	 * 
	 * @param customer - the customer to check.
	 * @param token    - the token of the admin that ask for the check.
	 * @throws InvalidUserException  - in case the customer has no email or no
	 *                               password.
	 * @throws InvalidLoginException - in case that the token is invalid or expired.
	 */
	private void checkIfCustomerIsValid(Customer customer, String token)
			throws InvalidUserException, InvalidLoginException {

		getService(token);

		if (customer.getEmail() == null || customer.getPassword() == null) {
			throw new InvalidUserException(
					"Unable to proceed. The customer received have no email or password.");
		}

	}

	/**
	 * This is a check method to verify that will be no email changes to avoid
	 * duplicates is the system.
	 * 
	 * @param customer - the customer to check.
	 * @param token    - the token of the admin that ask for the check.
	 * @throws InvalidLoginException    - in case that the token is invalid or
	 *                                  expired.
	 * @throws NoCustomerFoundException - in case of the customer id given not match
	 *                                  to any customer is the system.
	 * @throws IllegalChangeException   - in case of email not match to original
	 *                                  email on the system.
	 */
	private void checkIfCustomerEmailChanged(Customer customer, String token)
			throws InvalidLoginException, NoCustomerFoundException, IllegalChangeException {

		checkIfCustomerExists(customer.getId(), token);

		Customer originalCustomer = getService(token).findCustomerById(customer.getId());

		if (!originalCustomer.getEmail().equals(customer.getEmail())) {
			throw new IllegalChangeException(
					String.format("Unable to change the email from %s to %s, changing email address is not allowed.",
							originalCustomer.getEmail(), customer.getEmail()));
		}
	}

	/**
	 * This is a check method that checks by id if the customer is exists in the
	 * system.
	 * 
	 * @param id    - the id of the customer to by checked.
	 * @param token - the token of the admin that ask for the check.
	 * @throws InvalidLoginException    - in case that the token is invalid or
	 *                                  expired.
	 * @throws NoCustomerFoundException - in case the customer is not exists in the
	 *                                  system.
	 */
	private void checkIfCustomerExists(long id, String token) throws InvalidLoginException, NoCustomerFoundException {

		Customer customer = getService(token).findCustomerById(id);

		if (customer == null) {
			throw new NoCustomerFoundException(
					"Customer not found. Unable to find customer with that data given.");
		}
	}

	// ------------------------------------Company-Utils-------------------------------------------//

	/**
	 * This is a check method to verify that will be no email duplicates in the
	 * system.
	 * 
	 * @param company - the company to check.
	 * @param token   - the token of the admin that ask for the check.
	 * @throws InvalidLoginException       - in case that the token is invalid or
	 *                                     expired.
	 * @throws EmailAlreadyExistsException - in case the email already exists in the
	 *                                     system.
	 */
	private void checkIfCompanyEmailExists(Company company, String token)
			throws InvalidLoginException, EmailAlreadyExistsException {

		List<Company> allCompanies = getService(token).findAllCompanies();

		for (Company comp : allCompanies) {
			if (company.getEmail().equals(comp.getEmail())) {
				throw new EmailAlreadyExistsException(
						String.format("Unable to create new company, the email '%s' already exists in the system.",
								company.getEmail()));
			}
		}
	}

	/**
	 * This is a check method that checks if company have email and password.
	 * 
	 * @param company - the company to check.
	 * @param token   - the token of the admin that ask for the check.
	 * @throws InvalidUserException  - in case the company has no email or no
	 *                               password.
	 * @throws InvalidLoginException - in case that the token is invalid or expired.
	 */
	private void checkIfCompanyIsValid(Company company, String token)
			throws InvalidUserException, InvalidLoginException {

		getService(token);

		if (company.getEmail() == null || company.getPassword() == null) {
			throw new InvalidUserException(
					"Unable to proceed. The company received have no email or password.");
		}

	}

	/**
	 * This is a check method to verify that will be no email changes to avoid
	 * duplicates is the system.
	 * 
	 * @param company - the company to check.
	 * @param token   - the token of the admin that ask for the check.
	 * @throws InvalidLoginException   - in case that the token is invalid or
	 *                                 expired.
	 * @throws NoCompanyFoundException - in case of the company id given not match
	 *                                 to any company is the system.
	 * @throws IllegalChangeException  - in case of email not match to original
	 *                                 email on the system.
	 */
	private void checkIfCompanyEmailChanged(Company company, String token)
			throws InvalidLoginException, NoCompanyFoundException, IllegalChangeException {

		checkIfCompanyExists(company.getId(), token);

		Company originalCompany = getService(token).findCompanyById(company.getId());

		if (!originalCompany.getEmail().equals(company.getEmail())) {
			throw new IllegalChangeException(
					String.format(
							"Unable to change the email from '%s' to '%s', changing email address is not allowed.",
							originalCompany.getEmail(), company.getEmail()));
		}
	}

	/**
	 * This is a check method that checks by id if the company is exists in the
	 * system.
	 * 
	 * @param id    - the id of the company to by checked.
	 * @param token - the token of the admin that ask for the check.
	 * @throws InvalidLoginException   - in case that the token is invalid or
	 *                                 expired.
	 * @throws NoCompanyFoundException - in case the company is not exists in the
	 *                                 system.
	 */
	private void checkIfCompanyExists(long id, String token) throws InvalidLoginException, NoCompanyFoundException {

		Company company = getService(token).findCompanyById(id);

		if (company == null) {
			throw new NoCompanyFoundException("Company not found. Unable to find comapny with that data given.");
		}
	}

	// ------------------------------------Coupon-Utils-------------------------------------------//

	/**
	 * This is a check method to verify that will be no title duplicates in the
	 * system.
	 * 
	 * @param coupon - the coupon to check.
	 * @param token  - the token of the admin that ask for the check.
	 * @throws InvalidLoginException  - in case that the token is invalid or
	 *                                expired.
	 * @throws IllegalCouponException - in case the title already exists in the
	 *                                system.
	 */
	private void checkIfCouponTitleExists(Coupon coupon, String token)
			throws InvalidLoginException, IllegalCouponException {

		List<Coupon> allCoupons = getService(token).findAllCoupons();

		for (Coupon coup : allCoupons) {
			if (coup.getTitle().equals(coupon.getTitle())) {
				throw new IllegalCouponException(String.format(
						"The title '%s' is alredy exist. "
								+ "you need to change the title in order to create that coupon.",
						coup.getTitle()));
			}
		}
	}

	/**
	 * This is a check method to verify that coupon has title and end date in order
	 * to prevent checks in the system on null .
	 * 
	 * @param coupon - the coupon to check.
	 * @param token  - the token of the admin that ask for the check.
	 * @throws IllegalCouponException - in case the coupon has no title or end date.
	 * @throws InvalidLoginException  - in case that the token is invalid or
	 *                                expired.
	 */
	private void checkIfCouponIsValid(Coupon coupon, String token)
			throws IllegalCouponException, InvalidLoginException {

		getService(token);

		if (coupon.getTitle() == null || coupon.getEndDate() == null) {
			throw new IllegalCouponException("Unable to proceed. The coupon received have no title or end date.");
		}
	}

	/**
	 * This is a check method to verify that will be no title changes to avoid
	 * duplicates is the system. the method also verify that the company associated
	 * with the coupon is viable and in fact the same one associated in the request.
	 * 
	 * @param coupon    - the coupon to check.
	 * @param companyId - the id of the company associated with the coupon given.
	 * @param token     - the token of the admin that ask for the check.
	 * @throws InvalidLoginException   - in case that the token is invalid or
	 *                                 expired.
	 * @throws NoCompanyFoundException - in case of the company id given not match
	 *                                 to any company is the system.
	 * @throws NoCouponFoundException  - in case of the coupon id given to update
	 *                                 not match to any coupon in the system.
	 * @throws IllegalChangeException  - in case the company id given is not match
	 *                                 to the original company associated to the
	 *                                 coupon in the system.
	 * @throws IllegalCouponException  - in case the coupon title given not match
	 *                                 the one in the system.
	 */
	private void checkIfCouponValidToUpdate(Coupon coupon, long companyId, String token) throws InvalidLoginException,
			NoCompanyFoundException, NoCouponFoundException, IllegalChangeException, IllegalCouponException {

		checkIfCouponIsValid(coupon, token);

		Coupon originalCoupon = getService(token).findCouponById(coupon.getId());

		checkIfCompanyExists(companyId, token);

		checkIfCouponExists(coupon.getId(), token);

		if (originalCoupon.getCompany().getId() != companyId) {
			throw new IllegalChangeException(
					"Unable to update coupon, coupon not associated with the company given.");
		} else if (!originalCoupon.getTitle().equals(coupon.getTitle())) {
			throw new IllegalCouponException(String.format(
					"Unable to update title from '%s' to '%s', "
							+ "you can not change the title in order to update that coupon.",
					originalCoupon.getTitle(), coupon.getTitle()));
		}

	}

	/**
	 * This is a check method that checks by id if the coupon is exists in the
	 * system.
	 * 
	 * @param id    - the id of the coupon to by checked.
	 * @param token - the token of the admin that ask for the check.
	 * @throws InvalidLoginException- in case that the token is invalid or expired.
	 * @throws NoCouponFoundException - in case of the coupon id given is no match
	 *                                to any coupon in the system.
	 */
	private void checkIfCouponExists(long id, String token) throws InvalidLoginException, NoCouponFoundException {
		Coupon coupon = getService(token).findCouponById(id);

		if (coupon == null) {
			throw new NoCouponFoundException("Coupon not found. Unable to find coupon with the data given.");
		}
	}

}
