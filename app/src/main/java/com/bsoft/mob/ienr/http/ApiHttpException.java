package com.bsoft.mob.ienr.http;

public class ApiHttpException extends Exception {
	private static final long serialVersionUID = 1L;

	private String mExtra;

	public ApiHttpException(String message) {
		super(message);
	}

	public ApiHttpException(String message, String extra) {
		super(message);
		mExtra = extra;
	}

	public String getExtra() {
		return mExtra;
	}
}
