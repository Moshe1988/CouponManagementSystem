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

import com.cs.entity.Company;
import com.cs.entity.Coupon;
import com.cs.rest.ClientSession;
import com.cs.rest.ex.IllegalChangeException;
import com.cs.rest.ex.IllegalCouponException;
import com.cs.rest.ex.InvalidLoginException;
import com.cs.rest.ex.InvalidUserException;
import com.cs.rest.ex.NoCompanyFoundException;
import com.cs.rest.ex.NoCouponFoundException;
import com.cs.rest.service.CompanyService;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/api")
public class CompanyController {

	/**
	 * This map defined in 'RestConfiguration' in order to to access the same map
	 * from everywhere in the application.
	 */
	private Map<String, ClientSession> tokensMap;

	@Autowired
	public CompanyController(@Qualifier("tokens") Map<String, ClientSession> tokensMap) {
		this.tokensMap = tokensMap;
	}

	/**
	 * This function is getting the service of the company that stored in the
	 * 'tokensMap' inside the 'ClientSession' by the key 'token' given, or throws
	 * exception if the session expired or the token not exist. the function also
	 * notify the session that it been accessed.
	 * 
	 * @param token - the key of the value 'ClientSession' that stored in
	 *              'tokensMap'.
	 * @return CompanyService - the service of the company that matches with the
	 *         token that was given.
	 * @throws InvalidLoginException - if the token given not match to any
	 *                               'ClientSession' in the 'tokensMap' or the
	 *                               session was expired.
	 */
	private CompanyService getService(String token) throws InvalidLoginException {
		ClientSession clientSession = tokensMap.get(token);

		if (clientSession == null) {
			throw new InvalidLoginException("The login timed out, please login again.");
		}

		clientSession.accessed();

		return (CompanyService) clientSession.getService();
	}

	@GetMapping("/companies/{token}")
	public ResponseEntity<Company> getCurrentCompany(@PathVariable String token)
			throws InvalidLoginException, NoCompanyFoundException {

		checkIfCompanyExists(token);

		Company company = getService(token).findCurrentCompany();

		return ResponseEntity.ok(company);
	}

	@PutMapping("/companies/{token}")
	public ResponseEntity<Company> updateCompany(@RequestBody Company company, @PathVariable String token)
			throws InvalidLoginException, IllegalChangeException, InvalidUserException {

		checkIfCompanyValidToUpdate(company, token);

		getService(token).save(company);
		return ResponseEntity.ok(company);
	}

	@PostMapping("/companies/coupons/{token}")
	public ResponseEntity<Coupon> addCoupon(@RequestBody Coupon coupon, @PathVariable String token)
			throws InvalidLoginException, IllegalCouponException {

		checkIfCouponIsValid(coupon, token);

		checkIfCouponTitleExists(coupon, token);

		coupon.setId(0);
		getService(token).save(coupon);
		return ResponseEntity.ok(coupon);
	}

	@PutMapping("/companies/coupons/{token}")
	public ResponseEntity<Coupon> updateCoupon(@RequestBody Coupon coupon, @PathVariable String token)
			throws InvalidLoginException, NoCompanyFoundException, IllegalCouponException, NoCouponFoundException,
			IllegalChangeException {

		checkIfCompanyExists(token);

		checkIfCouponValidToUpdate(coupon, token);

		getService(token).save(coupon);
		return ResponseEntity.ok(coupon);
	}

	@GetMapping("/companies/coupons/{token}")
	public ResponseEntity<Collection<Coupon>> findCompanyCoupons(@PathVariable String token)
			throws InvalidLoginException {

		List<Coupon> companyCoupons = getService(token).findCompanyCoupons();

		if (companyCoupons.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(companyCoupons);
	}

	@GetMapping("/companies/coupons/{couponId}/{token}")
	public ResponseEntity<Coupon> findCouponById(@PathVariable long couponId, @PathVariable String token)
			throws InvalidLoginException, NoCouponFoundException, IllegalChangeException {

		checkIfCouponExists(couponId, token);

		Coupon coupon = getService(token).findCouponById(couponId);

		checkIfCouponAssociatedToCurrentCompany(token, coupon);

		return ResponseEntity.ok(coupon);
	}

	// --------------------------------------------Utils-----------------------------------------------//

	/**
	 * This is a check method to verify that the company is exists in the system.
	 * 
	 * @param token - the token of the company that send the request.
	 * @throws InvalidLoginException   - in case that the token is invalid or
	 *                                 expired.
	 * @throws NoCompanyFoundException - in case the company is not exists in the
	 *                                 system.
	 */
	private void checkIfCompanyExists(String token) throws InvalidLoginException, NoCompanyFoundException {

		Company company = getService(token).findCurrentCompany();

		if (company == null) {
			throw new NoCompanyFoundException("Unable to find company.");
		}
	}

	/**
	 * This is a check method to verify that coupon has title and end date in order
	 * to prevent checks in the system on null .
	 * 
	 * @param coupon - the coupon to check.
	 * @param token  - the token of the company that ask for the check.
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
	 * This is a check method that checks if company have email and password.
	 * 
	 * @param company - the company to check.
	 * @param token   - the token of the company that ask for the check.
	 * @throws InvalidUserException  - in case the company has no email or no
	 *                               password
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
	 * duplicates is the system and that the id of the company given is of the same
	 * one asking for update in order to avoid one customer updating another.
	 * 
	 * @param company - the company to check.
	 * @param token   - the token of the company that send the request.
	 * @throws InvalidLoginException  - in case that the token is invalid or
	 *                                expired.
	 * @throws IllegalChangeException - in case of email or id not match to original
	 *                                ones on the system.
	 * @throws InvalidUserException   - in case the company has no email or no
	 *                                password
	 */
	private void checkIfCompanyValidToUpdate(Company company, String token)
			throws InvalidLoginException, IllegalChangeException, InvalidUserException {

		checkIfCompanyIsValid(company, token);

		Company originalCompany = getService(token).findCurrentCompany();

		if (originalCompany.getId() != company.getId()) {
			throw new IllegalChangeException(
					"Unable to continue! id of the company is not match to the one currently log in.");
		} else if (!originalCompany.getEmail().equals(company.getEmail())) {
			throw new IllegalChangeException(
					String.format(
							"Unable to change the email from '%s' to '%s', changing email address is not allowed.",
							originalCompany.getEmail(), company.getEmail()));
		}
	}

	/**
	 * This is a check method to verify that will be no title duplicates in the
	 * system.
	 * 
	 * @param coupon- the coupon to check.
	 * @param token   - the token of the company that send the request.
	 * @throws InvalidLoginException   - in case that the token is invalid or
	 *                                 expired.
	 * @throws IllegalCouponException- in case the title already exists in the
	 *                                 system.
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
	 * This is a check method to verify that will be no title changes to avoid
	 * duplicates is the system. the method also verify that the company associated
	 * with the coupon is the same one asking for the update.
	 * 
	 * @param coupon - the coupon to check.
	 * @param token  - the token of the company that send the request.
	 * @throws InvalidLoginException  - in case that the token is invalid or
	 *                                expired.
	 * @throws IllegalCouponException - in case the company id given is not match to
	 *                                the original company associated to the coupon
	 *                                in the system.
	 * @throws NoCouponFoundException - in case of the coupon id given to update not
	 *                                match to any coupon in the system.
	 * @throws IllegalChangeException - in case the coupon given is not match to the
	 *                                current company asking for it.
	 */
	private void checkIfCouponValidToUpdate(Coupon coupon, String token)
			throws InvalidLoginException, IllegalCouponException, NoCouponFoundException, IllegalChangeException {

		checkIfCouponExists(coupon.getId(), token);

		checkIfCouponIsValid(coupon, token);

		Coupon originalCoupon = getService(token).findCouponById(coupon.getId());

		checkIfCouponAssociatedToCurrentCompany(token, originalCoupon);

		if (!originalCoupon.getTitle().equals(coupon.getTitle())) {
			throw new IllegalCouponException(String.format(
					"Unable to update title from '%s' to '%s', "
							+ "you can not change the title in order to update that coupon.",
					originalCoupon.getTitle(), coupon.getTitle()));
		}
	}

	/**
	 * This is a check method that verify that the company associated with the
	 * coupon is the same one asking for the coupon in order to avoid one company
	 * access or change anther company coupons.
	 * 
	 * @param token  - the token of the company that send the request.
	 * @param coupon - the coupon to check.
	 * @throws InvalidLoginException  - in case that the token is invalid or
	 *                                expired.
	 * @throws IllegalChangeException - in case the coupon given is not match to the
	 *                                company asking for it.
	 */
	private void checkIfCouponAssociatedToCurrentCompany(String token, Coupon coupon)
			throws InvalidLoginException, IllegalChangeException {

		if (coupon.getCompany().getId() != getService(token).findCurrentCompany().getId()) {
			throw new IllegalChangeException(
					"Unable to continue! "
							+ "id of the company that created that coupon is not match to the one currently log in.");
		}
	}

	/**
	 * This is a check method that checks by id if the coupon is exists in the
	 * system.
	 * 
	 * @param couponId - the id of the coupon to by checked.
	 * @param token    - the token of the company that send the request.
	 * @throws InvalidLoginException  - in case that the token is invalid or
	 *                                expired.
	 * @throws NoCouponFoundException - in case of the coupon id given is no match
	 *                                to any coupon in the system.
	 */
	private void checkIfCouponExists(long couponId, String token) throws InvalidLoginException, NoCouponFoundException {

		Coupon coupon = getService(token).findCouponById(couponId);

		if (coupon == null) {
			throw new NoCouponFoundException("Coupon not found. Unable to find coupon with the data given");
		}
	}

}
