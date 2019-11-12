package com.cs.rest.controller;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cs.entity.Coupon;
import com.cs.entity.Customer;
import com.cs.rest.ClientSession;
import com.cs.rest.ex.IllegalChangeException;
import com.cs.rest.ex.IllegalCouponException;
import com.cs.rest.ex.InvalidLoginException;
import com.cs.rest.ex.InvalidUserException;
import com.cs.rest.ex.NoCouponFoundException;
import com.cs.rest.ex.NoCustomerFoundException;
import com.cs.rest.service.CustomerService;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/api")
public class CustomerController {

	/**
	 * This map defined in 'RestConfiguration' in order to to access the same map
	 * from everywhere in the application.
	 */
	private Map<String, ClientSession> tokensMap;

	@Autowired
	public CustomerController(@Qualifier("tokens") Map<String, ClientSession> tokensMap) {
		this.tokensMap = tokensMap;
	}

	/**
	 * This function is getting the service of the customer that stored in the
	 * 'tokensMap' inside the 'ClientSession' by the key 'token' given, or throws
	 * exception if the session expired or the token not exist. the function also
	 * notify the session that it been accessed.
	 * 
	 * @param token - the key of the value 'ClientSession' that stored in
	 *              'tokensMap'.
	 * @return CustomerService - the service of the customer that matches the token
	 *         that was given.
	 * @throws InvalidLoginException - if the token given not match to any
	 *                               'ClientSession' in the 'tokensMap' or the
	 *                               session was expired.
	 */
	private CustomerService getService(String token)
			throws InvalidLoginException {
		ClientSession clientSession = tokensMap.get(token);

		if (clientSession == null) {
			throw new InvalidLoginException("The login timed out, please login again.");
		}

		clientSession.accessed();

		return (CustomerService) clientSession.getService();
	}

	@GetMapping("/customers/{token}")
	public ResponseEntity<Customer> getCurrentCustomer(@PathVariable String token)
			throws InvalidLoginException, NoCustomerFoundException {

		checkIfCustomerExists(token);

		Customer customer = getService(token).findCurrentCustomer();

		return ResponseEntity.ok(customer);
	}

	@PutMapping("/customers/{token}")
	public ResponseEntity<Customer> updateCustomer(@RequestBody Customer customer, @PathVariable String token)
			throws InvalidLoginException, IllegalChangeException, InvalidUserException {

		checkIfCustomerValidToUpdate(customer, token);

		getService(token).save(customer);
		return ResponseEntity.ok(customer);
	}

	@GetMapping("/customers/customerCoupons/{token}")
	public ResponseEntity<Collection<Coupon>> findCustomerCoupons(@PathVariable String token)
			throws InvalidLoginException {

		List<Coupon> customerCoupons = getService(token).findCustomerCoupons();

		if (customerCoupons.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(customerCoupons);
	}

	@PostMapping("/customers/purchaseCoupon/{couponId}/{token}")
	public ResponseEntity<Coupon> purchaseCoupon(@PathVariable long couponId, @PathVariable String token)
			throws InvalidLoginException, IllegalCouponException, NoCouponFoundException {

		checkIfCouponExists(couponId, token);

		checkIfCouponInStack(couponId, token);

		checkIfCouponPurchased(couponId, token);

		Coupon couponPurchased = getService(token).purchaseCoupon(couponId);

		return ResponseEntity.ok(couponPurchased);
	}

	@GetMapping("/customers/coupons/{token}")
	public ResponseEntity<Collection<Coupon>> findAllCoupons(@PathVariable String token)
			throws InvalidLoginException {

		List<Coupon> allCoupons = getService(token).findAllCoupons();

		if (allCoupons.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(allCoupons);
	}

	@GetMapping("/customers/coupons/{couponId}/{token}")
	public ResponseEntity<Coupon> findCouponById(@PathVariable long couponId, @PathVariable String token)
			throws InvalidLoginException, NoCouponFoundException {

		checkIfCouponExists(couponId, token);

		Coupon coupon = getService(token).findCouponById(couponId);

		return ResponseEntity.ok(coupon);
	}

	// --------------------------------------------Utils-----------------------------------------------//

	/**
	 * This is a check method to verify that the customer is exists in the system.
	 * 
	 * @param token - the token of the company that send the request.
	 * @throws InvalidLoginException    - in case that the token is invalid or
	 *                                  expired.
	 * @throws NoCustomerFoundException
	 */
	private void checkIfCustomerExists(String token) throws InvalidLoginException, NoCustomerFoundException {

		Customer customer = getService(token).findCurrentCustomer();

		if (customer == null) {
			throw new NoCustomerFoundException("Unable to find customer.");
		}
	}

	/**
	 * This is a check method to verify that will be no email changes to avoid
	 * duplicates is the system and that the id of the customer given is of the same
	 * one asking for update in order to avoid one customer updating another.
	 * 
	 * @param customer - the customer to check.
	 * @param token    - the token of the company that send the request.
	 * @throws InvalidLoginException  - in case that the token is invalid or
	 *                                expired.
	 * @throws IllegalChangeException - in case of email or id not match to original
	 *                                ones on the system.
	 * @throws InvalidUserException   - in case the customer has no email or no
	 *                                password.
	 */
	private void checkIfCustomerValidToUpdate(Customer customer, String token)
			throws InvalidLoginException, IllegalChangeException, InvalidUserException {

		checkIfCustomerIsValid(customer, token);

		Customer originalCustomer = getService(token).findCurrentCustomer();

		if (originalCustomer.getId() != customer.getId()) {
			throw new IllegalChangeException(
					"Unable to continue! id of the customer is not match to the one currently log in.");
		} else if (!originalCustomer.getEmail().equals(customer.getEmail())) {
			throw new IllegalChangeException(
					String.format(
							"Unable to change the email from '%s' to '%s', changing email address is not allowed.",
							originalCustomer.getEmail(), customer.getEmail()));
		}
	}

	/**
	 * This is a check method that checks if customer have email and password.
	 * 
	 * @param customer - the customer to check.
	 * @param token    - the token of the customer that ask for the check.
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
	 * This is a check method to verify that will be no duplicate purchases of
	 * coupons.
	 * 
	 * @param couponId - the id of the coupon to by checked.
	 * @param token    - the token of the company that send the request.
	 * @throws InvalidLoginException  - in case that the token is invalid or
	 *                                expired.
	 * @throws NoCouponFoundException - in case of the coupon id given is no match
	 *                                to any coupon in the system.
	 * @throws IllegalCouponException - in case that coupon already purchased.
	 */
	private void checkIfCouponPurchased(long couponId, String token)
			throws InvalidLoginException, IllegalCouponException {

		List<Coupon> customerCoupons = getService(token).findCustomerCoupons();

		for (Coupon coup : customerCoupons) {
			if (couponId == coup.getId()) {
				throw new IllegalCouponException(String.format("Unable to purchase coupon '%s'."
						+ " coupon already purchased, duplicate are not allowed.", coup.getTitle()));
			}
		}
	}

	/**
	 * This is a check method that will check if the amount of coupon in above 0.
	 * 
	 * @param couponId - the id of the coupon to by checked.
	 * @param token    - the token of the company that send the request.
	 * @throws IllegalCouponException - in case that coupon amount is 0.
	 * @throws InvalidLoginException  - in case that the token is invalid or
	 *                                expired.
	 */
	private void checkIfCouponInStack(long couponId, String token)
			throws IllegalCouponException, InvalidLoginException {
		Coupon coupon = getService(token).findCouponById(couponId);

		if (coupon.getAmount() <= 0) {
			throw new IllegalCouponException(String.format("Unable to purchase coupon '%s'."
					+ " coupon out of stack.", coupon.getTitle()));
		}
	}

	/**
	 * This is a check method that checks by id if the coupon is exists in the
	 * system.
	 * 
	 * @param couponId - the id of the coupon to by checked.
	 * @param token    - the token of the company that send the request.
	 * @throws NoCouponFoundException - in case of the coupon id given is no match
	 *                                to any coupon in the system.
	 * @throws InvalidLoginException  - in case that the token is invalid or
	 *                                expired.
	 */
	private void checkIfCouponExists(long couponId, String token) throws NoCouponFoundException, InvalidLoginException {

		Coupon coupon = getService(token).findCouponById(couponId);

		if (coupon == null) {
			throw new NoCouponFoundException("Coupon not found. Unable to find coupon with the data given");
		}
	}

}
