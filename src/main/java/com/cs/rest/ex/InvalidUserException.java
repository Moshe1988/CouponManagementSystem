package com.cs.rest.ex;

@SuppressWarnings("serial")
public class InvalidUserException extends Exception {

	public InvalidUserException(String msg) {
		super(msg);
	}
}
