package com.cs.rest.ex;

@SuppressWarnings("serial")
public class NoCouponFoundException extends Exception {
	
	public NoCouponFoundException(String msg) {
		super(msg);
	}
}
