package downloder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import downloder.exception.DownloaderException;

public class Download extends Thread {
	Logger logger = LoggerFactory.getLogger(Download.class);
	private URLConnection conn;
	private long contentLength;
	private InputStream stream;
	private long downloaded;
	private static final int MAX_SIZE = 1024;
	private String path;
	private String fileNameToBeSaved;
	private String SystemPathToSaveFile = "/Users/durgeshkumar/Documents/sts/learning/downloder/src/main/java/downloder";
	private int read = 0;
	private boolean isResumable = false;
	private float percentageDownload;
	private boolean isInterrupted = false;
	private boolean isCompleted = false;

	public Download(String path, String pathToSaveFile) throws Exception {
		if (pathToSaveFile != null && !pathToSaveFile.isEmpty())
			this.SystemPathToSaveFile = pathToSaveFile;
		logger.info("setting path to download : " + path);
		this.path = path;
		this.setFileName();
		getConnection(path);
		this.contentLength = this.getContentLength();
		isResumable();
		if (isResumable) {
			resumingDownload();
		}
		this.stream = this.conn.getInputStream();

	}

	public void run() {
		try {
			super.setName(fileNameToBeSaved);
			this.startDownload();
			this.isCompleted = true;
		} catch (IOException e) {
			logger.warn(this.getName() + ": gave exception - " + e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			this.isInterrupted = true;
			logger.warn(this.getName() + " : got interrupted.");
		}

	}

	private void startDownload() throws IOException, InterruptedException {
		// skipBytesFromStream();

		logger.info("*************** STARTING DOWNLOAD FOR : " + this.fileNameToBeSaved + " **************");
		while (downloaded < contentLength) {
			final byte buffer[] = new byte[MAX_SIZE];
			try {
				read = stream.read(buffer);

			} catch (Exception e) {
				if (e.getMessage().equals("Connection reset")) {
					System.out.println("Reconnecting......");

					this.reconnect();

				}
				e.printStackTrace();
			}
			Thread t = new Thread(new Runnable() {

				public void run() {

					try {

						RandomAccessFile f = new RandomAccessFile(SystemPathToSaveFile + fileNameToBeSaved, "rw");
						f.seek(f.length());
						if (read != -1)
							f.write(buffer, 0, read);
						f.close();
					} catch (IOException e) {

						e.printStackTrace();
					}
				}

			});
			t.start();
			t.join();
			if (read != -1)
				downloaded += read;
			// System.out.print();
			setPercentDownoaded();
			if (this.percentageDownload == 100) {
				System.out.print("");
				;
			}
		}

	}

	private void setPercentDownoaded() {

		float percentDownload = ((float) (downloaded) / contentLength) * 100;
		if (percentDownload == 100)
			this.isCompleted = true;
		System.out.println(percentDownload);
		this.percentageDownload = percentDownload;

	}

	public float getPercentDownoaded() {
		return this.percentageDownload;
	}

	/**
	 * Resuming the downloads.
	 * 
	 * @throws DownloaderException
	 */
	private void resumingDownload() throws DownloaderException {
		logger.info("Trying to resume the download if download is resumable.");
		if (isResumable) {
			if (isFilePresent()) {
				logger.info("File Present. Resuming the download");
				long availableSize = getFileSizeDownloaded();
				if (availableSize < contentLength)
					requestPartialDownload(availableSize, contentLength);
				else {
					logger.info(DownloaderException.NOTHINGTORESUME);
					throw new DownloaderException(DownloaderException.NOTHINGTORESUME);
				}
				this.downloaded = availableSize;
			}
		} else {
			logger.info("cannot resume media/file :" + fileNameToBeSaved);
		}
	}

	/**
	 * This method set request property to fetch the partial file.
	 * 
	 * @param start bytes from which file should be downloaded.
	 * @param end   bytes till where the file should be downloaded.
	 */
	private void requestPartialDownload(long start, long end) {
		logger.info("requesting partial media/file form " + start + " to " + end);
		this.conn.setRequestProperty("Range", "bytes=" + start + "-" + end);
	}

	/**
	 * Fetches the length of the downloaded file.
	 * 
	 * @return length of the downloaded file in bytes.
	 */
	private long getFileSizeDownloaded() {
		logger.info("getting the size for downloaded for : " + fileNameToBeSaved);
		File file = new File(SystemPathToSaveFile + fileNameToBeSaved);
		long size = 0l;
		if (file.exists())
			size = file.length();
		logger.info("size for media/file  downloaded is : " + size);
		return size;
	}

	/**
	 * Opens connection to the provided path.
	 * 
	 * @param path Path to which connection is to be opened.
	 * @throws IOException
	 */
	private void getConnection(String path) throws IOException {
		logger.info("getting connection for path: " + path);
		URL url = new URL(path);
		this.conn = (HttpURLConnection) url.openConnection();
		logger.info("connection made to path : " + path);
	}

	/**
	 * Fetches content length from the temporary connection.
	 * 
	 * @return Length of the file/media in bytes
	 * @throws IOException
	 */
	private long getContentLength() throws IOException {
		logger.info("getting content length for file : " + this.fileNameToBeSaved);
		HttpURLConnection conn = getTempConnection(this.path);
		long cl = Long.parseLong(conn.getHeaderField("Content-Length"));
		conn.disconnect();
		logger.info("Content length for file : " + this.fileNameToBeSaved + " " + cl + " bytes");
		return cl;
	}

	/**
	 * This is temporary connection is needed to get all headers property like content length, is resumable etc as when
	 * connection is made with cannot set the requestproperty. Note : When ever this connection must be closed using the
	 * disconnect() method of HTTPURLConnection class.
	 * 
	 * @param path - String http path to which connection is to be made
	 * @return
	 * @throws IOException
	 */
	private HttpURLConnection getTempConnection(String path) throws IOException {
		logger.info("getting Temporary connection for path: " + path);
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		logger.info("connection made to path : " + path);
		return conn;
	}

	private void reconnect() {
		try {
			getConnection(path);
			stream = this.conn.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setFileName() {
		logger.info("Getting file name for media/file");
		String arr[] = path.split("/");
		this.fileNameToBeSaved = arr[arr.length - 1];
		logger.info("File name for the media/file is : " + this.fileNameToBeSaved);
	}

	private boolean isFilePresent() {
		logger.info("Checking media/file present or not.");
		File file = new File(SystemPathToSaveFile + fileNameToBeSaved);
		logger.info(fileNameToBeSaved + " media/file already present : " + file.exists());
		return file.exists();
	}

	private void skipBytesFromStream() throws IOException {
		// long bytesToSkip = 0;
		if (isFilePresent()) {
			File file = new File(SystemPathToSaveFile + fileNameToBeSaved);
			System.out.println(file.length());
			this.downloaded = file.length();
			stream.skip(file.length());
		}
	}

	private void isResumable() throws IOException {
		logger.info("getting connection is resumable or not. ");
		HttpURLConnection conn = getTempConnection(this.path);
		String result = conn.getHeaderField("Accept-Ranges");
		if ("bytes".equals(result)) {
			logger.info("Connection is resumable.");
			this.isResumable = true;
		} else {
			logger.info("Connection is not resumable.");
		}
		conn.disconnect();
	}

	public void setSystemPathToSave(String pathToSaveFile) {
		if (pathToSaveFile != null || pathToSaveFile.isEmpty())
			this.SystemPathToSaveFile = pathToSaveFile;
	}

	public String getSystemPathToSave() {

		return this.SystemPathToSaveFile;
	}

	public String getPath() {
		return path;
	}

	public String getFileNameToBeSaved() {
		return fileNameToBeSaved;
	}

	public boolean isInterrupted() {
		return isInterrupted;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

}
