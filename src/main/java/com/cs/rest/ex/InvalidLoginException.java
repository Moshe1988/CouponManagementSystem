package com.cs.rest.ex;

@SuppressWarnings("serial")
public class InvalidLoginException extends Exception {

	public InvalidLoginException(String msg) {
		super(msg);
	}
}
