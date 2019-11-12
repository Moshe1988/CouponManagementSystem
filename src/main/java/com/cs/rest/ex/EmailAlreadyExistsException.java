package com.cs.rest.ex;

@SuppressWarnings("serial")
public class EmailAlreadyExistsException extends Exception {

	public EmailAlreadyExistsException(String msg) {
		super(msg);
	}
}
