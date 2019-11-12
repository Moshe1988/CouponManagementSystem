package com.cs.rest.ex;

import org.springframework.http.HttpStatus;

public class CouponSystemErrorResponse {

	private HttpStatus status;
	private String message;
	private long timestemp;

	public CouponSystemErrorResponse(HttpStatus status, String message, long timestemp) {
		this.status = status;
		this.message = message;
		this.timestemp = timestemp;
	}

	public static CouponSystemErrorResponse now(HttpStatus status, String message) {
		return new CouponSystemErrorResponse(status, message, System.currentTimeMillis());
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getTimestemp() {
		return timestemp;
	}

	public void setTimestemp(long timestemp) {
		this.timestemp = timestemp;
	}

}
