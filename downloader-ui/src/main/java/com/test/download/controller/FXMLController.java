package com.test.download.controller;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.gluonhq.charm.glisten.control.ProgressBar;
import com.test.download.Utils;
import com.test.download.exception.DownloadException;
import com.test.download.model.FileModel;

//import com.gluonhq.charm.glisten.control.ProgressBar;

import downloder.Download;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLController implements Initializable {
	// Displays the input text that is used to paste the download link.
	@FXML
	private TextField linktxt;

	@FXML
	private Button downloadbtn;
	//Represent the table that use to display the list of downloading/dowloaded file.
	@FXML
	private TableView<FileModel> downloadListTable;
	//Holds the name of dowloading or downloaded file.
	@FXML
	private TableColumn<FileModel, Label> fileNameClmn;
	//Holdes the progress bar representing the preogress of the download.
	@FXML
	private TableColumn<FileModel, ProgressBar> progressClmn;
	//Holds percentage of downloaded file
	@FXML
	private TableColumn<FileModel, Label> percentClmn;
	//Holds label representing values like completed/downloading or paused.
	@FXML
	private TableColumn<FileModel, Label> statusClmn;
	//hold the vaue button to perform action like Resume and Pause
	@FXML
	private TableColumn<FileModel, Button> actionClmn;
	//Holds the system path where filed needed to be saved after downloading.
	private String pathToSaveFile = "/Users/durgeshkumar/Downloads/Downloader/";
	//Holds the element that needed to be displayed in downloadListTable.
	private ObservableList<FileModel> FileDownloadList = FXCollections.observableArrayList();
	// Holds the download links that are requested for download with key as the dowload link provided by app.
	private static Map<String, FileModel> downloadListMap = new HashMap<>();
	 FileModel fileModel ;
	@FXML
	void callDownload(ActionEvent event) throws Exception {
		String pathFromToDownload = this.linktxt.getText();

		if (validateLink(pathFromToDownload)) {
			System.out.print("link is : " + pathFromToDownload);
			final Download downloadThread;
			try {
				downloadThread = new Download(pathFromToDownload, pathToSaveFile);
				downloadThread.start();
				fileModel = new FileModel(downloadThread);
				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {
						while (downloadThread.isAlive()) {
							double value =downloadThread.getPercentDownoaded() / 100;
						//	fileModel.setCompleted(downloadThread.isCompleted());
							fileModel.getProgressBar().setProgress(value);
							
							// listModel.getPercentLbl().setText(String.valueOf(downloadThread.getPercentDownoaded()));
						}
					}
				});
				t.start();
				downloadListMap.put(fileModel.getPathToDownload(), fileModel);
				FileDownloadList.add(fileModel);
			} catch (Exception e) {
				showExceptionBox(e.getMessage());
			}
			
		}

	}
	
	/**
	 * Perform below validation
	 * <li> It check for emppty link.</li>
	 * <li> Check if download link is already present in download list or not.
	 * If any of the the checks are failed then show the exception box.
	 * @param pathToDownloadFrom
	 * @return retrun true if all validation are are succes else false.
	 */
	private boolean validateLink(String pathToDownloadFrom) {
		boolean flag = true;
		try {
			//checking for empty link
			Utils.CheckForEmptyLink(pathToDownloadFrom);
			//checking if file is present in download list.
			Utils.checkFileAlreadyInList(pathToDownloadFrom, downloadListMap);
		} catch (DownloadException e) {
			flag = false;
			// Display the Exception box
			showExceptionBox(e.getMessage());

		}
		return flag;

	}
	/**
	 * It creates the Exception box and dilpay the exception message.
	 * @param message -Exception message to be displayed.
	 */
	private void showExceptionBox(String message) {

		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/exception/ExceptionBox.fxml"));
			Parent parent = fxmlLoader.load();
			ExceptionController exceptionController = fxmlLoader.<ExceptionController>getController();
			exceptionController.setExceptionMsg(message);
			Scene scene = new Scene(parent);
			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setScene(scene);
			stage.showAndWait();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * This method is responsible for initialization during the app start up.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
		//Mapping listModel property with table columns.
		fileNameClmn.setCellValueFactory(new PropertyValueFactory<FileModel, Label>("fileNameLbl"));
		progressClmn.setCellValueFactory(new PropertyValueFactory<FileModel, ProgressBar>("progressBar"));

		percentClmn.setCellValueFactory(new PropertyValueFactory<FileModel, Label>("percentLbl"));
		statusClmn.setCellValueFactory(new PropertyValueFactory<FileModel, Label>("statusLbl"));
		actionClmn.setCellValueFactory(new PropertyValueFactory<FileModel, Button>("actionBtn"));
		downloadListTable.setItems(FileDownloadList);
	}

}
