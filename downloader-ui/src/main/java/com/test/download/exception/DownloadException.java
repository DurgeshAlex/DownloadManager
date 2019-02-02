package com.test.download.exception;

/**
 * This class deals with the download exception. Every exception related to downloading should use this class
 * 
 * @author durgeshkumar
 *
 */
public class DownloadException extends Exception {

	public DownloadException() {
		super();
	}

	public DownloadException(String message) {
		super(message);
	}
}
