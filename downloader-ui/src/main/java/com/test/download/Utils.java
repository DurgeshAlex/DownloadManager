package com.test.download;

import java.util.Map;

import com.test.download.exception.DownloadException;
import com.test.download.exception.ExceptionMsg;
import com.test.download.model.FileModel;

public class Utils {
	/**
	 * Chek if file given to dowloader to download is alredy present in download list. --It validate the presence of
	 * file in current downloading list by checking path is present as in haspmap or not.
	 * 
	 * @param path
	 * @param downloadLIst
	 * @throws DownloadException
	 */
	public static void checkFileAlreadyInList(String path, Map<String, FileModel> downloadLIst) throws DownloadException {
		if(downloadLIst.containsKey(path)) {
			throw new DownloadException(ExceptionMsg.ALREADYPRESENT);
		}
	}
	/**
	 * Check for provide link is Empty Or null
	 * @param path
	 * @throws DownloadException
	 */
	
	public static void CheckForEmptyLink(String path) throws DownloadException {
		if(path == null || path.isEmpty()) {
			throw new DownloadException(ExceptionMsg.EMPTY);
		}
	}
}
