package com.cs.rest.ex;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.cs.rest.controller.AdminController;
import com.cs.rest.controller.CompanyController;
import com.cs.rest.controller.CustomerController;
import com.cs.rest.controller.LoginController;

@ControllerAdvice(
		assignableTypes = {
				LoginController.class,
				CustomerController.class,
				CompanyController.class,
				AdminController.class
		})
public class CouponSystemExceptionHandler {

	@ExceptionHandler(InvalidLoginException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ResponseBody
	public CouponSystemErrorResponse handelUnauthorized(InvalidLoginException ex) {
		return CouponSystemErrorResponse.now(HttpStatus.UNAUTHORIZED,
				String.format("Unauthorized: %s", ex.getMessage()));
	}
	
	@ExceptionHandler(EmailAlreadyExistsException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	@ResponseBody
	public CouponSystemErrorResponse handelConflict(EmailAlreadyExistsException ex) {
		return CouponSystemErrorResponse.now(HttpStatus.CONFLICT,
				String.format("Conflict: %s", ex.getMessage()));
	}
	
	@ExceptionHandler(IllegalChangeException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ResponseBody
	public CouponSystemErrorResponse handelForbidden(IllegalChangeException ex) {
		return CouponSystemErrorResponse.now(HttpStatus.FORBIDDEN,
				String.format("Forbidden: %s", ex.getMessage()));
	}
	
	@ExceptionHandler(IllegalCouponException.class)
	@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
	@ResponseBody
	public CouponSystemErrorResponse handelNotAcceptable(IllegalCouponException ex) {
		return CouponSystemErrorResponse.now(HttpStatus.NOT_ACCEPTABLE,
				String.format("Not Acceptable: %s", ex.getMessage()));
	}
	
	@ExceptionHandler(InvalidUserException.class)
	@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
	@ResponseBody
	public CouponSystemErrorResponse handelNotAcceptable(InvalidUserException ex) {
		return CouponSystemErrorResponse.now(HttpStatus.NOT_ACCEPTABLE,
				String.format("Not Acceptable: %s", ex.getMessage()));
	}
	
	@ExceptionHandler(NoCompanyFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public CouponSystemErrorResponse handelNotFound(NoCompanyFoundException ex) {
		return CouponSystemErrorResponse.now(HttpStatus.NOT_FOUND,
				String.format("Not Found: %s", ex.getMessage()));
	}
	
	@ExceptionHandler(NoCouponFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public CouponSystemErrorResponse handelNotFound(NoCouponFoundException ex) {
		return CouponSystemErrorResponse.now(HttpStatus.NOT_FOUND,
				String.format("Not Found: %s", ex.getMessage()));
	}
	
	@ExceptionHandler(NoCustomerFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public CouponSystemErrorResponse handelNotFound(NoCustomerFoundException ex) {
		return CouponSystemErrorResponse.now(HttpStatus.NOT_FOUND,
				String.format("Not Found: %s", ex.getMessage()));
	}
	
}
