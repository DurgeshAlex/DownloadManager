package com.test.download.model;

import com.gluonhq.charm.glisten.control.ProgressBar;

import downloder.Download;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * @author durgeshkumar
 *
 */
/**
 * @author durgeshkumar
 *
 */
public class FileModel {
	private Label fileNameLbl;
	private ProgressBar progressBar;
	private Label percentLbl;
	private Label statusLbl;
	private Button actionBtn;
	private Download downloadFileThread;
	private String pathToDownload;
	private String StrfileName;
	private String strPercent;
	public boolean isInterrupted;
	public boolean isCompleted;
	
	public FileModel(Download downloadFileThread) {
		this.downloadFileThread = downloadFileThread;
		this.StrfileName = this.downloadFileThread.getFileNameToBeSaved();
		this.strPercent = String.valueOf(this.downloadFileThread.getPercentDownoaded());
		this.pathToDownload = this.downloadFileThread.getPath();
		this.setFileNameLbl(new Label(this.StrfileName));
		this.setProgressBar(new ProgressBar());
		this.setPercentLbl(new Label());
		this.setActionBtn(new Button(Action.Pause.toString()));
		this.setStatusLbl(new Label(Status.Downloading.toString()));
	}
	
	
	public Label getFileNameLbl() {
		return fileNameLbl;
	}
	private void setFileNameLbl(Label fileNameLbl) {
		fileNameLbl.setPrefHeight(this.prefHeight);
		this.fileNameLbl = fileNameLbl;
	}
	public ProgressBar getProgressBar() {
		return progressBar;
	}
	private void setProgressBar(ProgressBar progressBar) {
		progressBar.setPrefHeight(this.prefHeight);
		progressBar.setPrefWidth(ProgressBar.USE_COMPUTED_SIZE);

		this.progressBar = progressBar;
	}
	public Label getPercentLbl() {
		return percentLbl;
	}
	private void setPercentLbl(Label percentLbl) {
		percentLbl.setPrefHeight(this.prefHeight);

		this.percentLbl = percentLbl;
	}
	public Label getStatusLbl() {
		return statusLbl;
	}
	private void setStatusLbl(Label statusLbl) {
		statusLbl.setPrefHeight(this.prefHeight);

		this.statusLbl = statusLbl;
	}
	public Button getActionBtn() {
		return actionBtn;
	}
	private void setActionBtn(Button actionBtn) {
		actionBtn.setPrefHeight(this.prefHeight);
		actionBtn.setId(pathToDownload);
		actionBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				try {
					resumePause(e);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		this.actionBtn = actionBtn;
	}
	public String getPathToDownload() {
		return pathToDownload;
	}
	public void setPathToDownload(String pathToDownload) {
		this.pathToDownload = pathToDownload;
	}

	public Download getDownloadFileThread() {
		return downloadFileThread;
	}
	void resumePause(ActionEvent event) throws Exception {
		Button btn = (Button) event.getSource();
		String name = btn.getText();
		if (name.equals(Action.Pause.toString())) {
			btn.setText(Action.Resume.toString());
			this.downloadFileThread.interrupt();
		} else {
			btn.setText(Action.Pause.toString());
			resumeDownload();
		}

	}

	private void resumeDownload() throws Exception {

		// System.out.print("link is : " + pathFromToDownload);
		final ProgressBar progressBar = getProgressBar();
		final Download downloadThread = new Download(this.pathToDownload, this.downloadFileThread.getSystemPathToSave());
		downloadThread.start();
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				while (downloadThread.isAlive()) {
					progressBar.setProgress(downloadThread.getPercentDownoaded() / 100);
					//percentLbl.setText(String.valueOf(downloadThread.getPercentDownoaded()));
				}
			}
		});
		t.start();

	}
	
	public boolean isInterrupted() {
		return isInterrupted;
	}


	public void setInterrupted(boolean isInterrupted) {
		if(isInterrupted== true) {
			this.getStatusLbl().setText(Status.Paused.toString());
		}
		this.isInterrupted = isInterrupted;
	}


	public boolean isCompleted() {
		return isCompleted;
	}


	public void setCompleted(boolean isCompleted) {
		if(isInterrupted== true) {
			this.getStatusLbl().setText(Status.Downloading.toString());
		}
		this.isCompleted = isCompleted;
	}

	enum Status {
		Downloading, Paused, Completed;
	}
	enum Action{
		Pause, Resume;
	}
	
	private static final double prefHeight = 15;

//	public void setPercentLblTxt(String value) {
//		this.percentLbl.labelForProperty().bind(observable);Text(value);
//	}
	
}
