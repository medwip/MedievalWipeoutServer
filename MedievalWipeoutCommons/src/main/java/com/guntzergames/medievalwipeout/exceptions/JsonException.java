package com.guntzergames.medievalwipeout.exceptions;

public class JsonException extends GameException {

	private static final long serialVersionUID = 2249795889686511032L;

	public JsonException() {
	}

	public JsonException(String message, Throwable cause) {
		super(message, cause);
	}

	public JsonException(String message) {
		super(message);
	}

	public JsonException(Throwable cause) {
		super(cause);
	}

}
