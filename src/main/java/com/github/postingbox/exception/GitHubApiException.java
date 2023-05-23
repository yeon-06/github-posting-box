package com.github.postingbox.exception;

public class GitHubApiException extends RuntimeException {

	private static final String MESSAGE_FORMAT = "[GitHub Api] %s";

	public GitHubApiException(String message) {
		super(String.format(MESSAGE_FORMAT, message));
	}

	public GitHubApiException(String message, Throwable cause) {
		super(String.format(MESSAGE_FORMAT, message), cause);
	}
}
