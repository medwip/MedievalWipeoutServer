package com.guntzergames.medievalwipeout.exceptions;

public class UnknownSkillException extends GameException {

	private static final long serialVersionUID = 3452750322530512437L;

	public UnknownSkillException() {
	}

	public UnknownSkillException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownSkillException(String message) {
		super(message);
	}

	public UnknownSkillException(Throwable cause) {
		super(cause);
	}

}
