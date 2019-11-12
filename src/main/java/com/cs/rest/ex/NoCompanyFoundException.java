package com.cs.rest.ex;

@SuppressWarnings("serial")
public class NoCompanyFoundException extends Exception {
	public NoCompanyFoundException(String msg) {
		super(msg);
	}
}
