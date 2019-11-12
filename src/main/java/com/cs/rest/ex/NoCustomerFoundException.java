package com.cs.rest.ex;

@SuppressWarnings("serial")
public class NoCustomerFoundException extends Exception {
	public NoCustomerFoundException(String msg) {
		super(msg);
	}
}
