package com.cs.rest.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cs.rest.ClientSession;
import com.cs.rest.CouponSystem;
import com.cs.rest.ex.InvalidLoginException;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class LoginController {

	private static final int LENGTH_TOKEN = 15;

	/**
	 * This map defined in 'RestConfiguration' in order to to access the same map
	 * from everywhere in the application.
	 */
	private Map<String, ClientSession> tokensMap;
	private CouponSystem couponSystem;

	@Autowired
	public LoginController(CouponSystem couponSystem, @Qualifier("tokens") Map<String, ClientSession> tokensMap) {
		this.couponSystem = couponSystem;
		this.tokensMap = tokensMap;
	}

	/**
	 * This method will login a user via 'CouponSystem' if the method 'login' of
	 * 'CouponSystem' will return a 'ClientSession' this method will store it in the
	 * 'tokensMap' and generate token in order to become the key for the session in
	 * the 'tokensMap'.
	 * 
	 * @param email    - the email of the user that want to login.
	 * @param password - the password of the user that want to login.
	 * @param type     - the type of the user that want to login.
	 * @return the token generated.
	 * @throws InvalidLoginException
	 */
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password,
			@RequestParam String type) throws InvalidLoginException {
		
		ClientSession session = couponSystem.login(email, password, type);
		
		String token = generateToken();

		tokensMap.put(token, session);

		return ResponseEntity.ok(token);
	}

	@DeleteMapping("/logout/{token}")
	public void logout(@PathVariable String token) {
		tokensMap.remove(token);
	}

	/**
	 * This method is for the client side to know when last access in order to show
	 * it in the client side as auto logout.
	 * 
	 * @param token - the token of the user that ask for the last access.
	 * @return time of the session last access in milliseconds.
	 * @throws InvalidLoginException
	 */
	@GetMapping("/login/getLastAccessed/{token}")
	public long getlastAccessed(@PathVariable String token) throws InvalidLoginException {
		ClientSession session = tokensMap.get(token);

		if (session == null) {
			throw new InvalidLoginException("The login timed out, please login again.");
		}

		session.accessed();

		return session.getLastAccessedMillis();
	}

	private String generateToken() {
		return UUID.randomUUID()
				.toString()
				.replaceAll("-", "")
				.substring(0, LENGTH_TOKEN);
	}
}