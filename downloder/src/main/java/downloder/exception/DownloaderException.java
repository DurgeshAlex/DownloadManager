package downloder.exception;

public class DownloaderException extends Exception {
	public static final String NOTHINGTORESUME ="Nothing to RESUME.File already downloaded";
	public DownloaderException(String msg) {
		super(msg);
	}

}
