package com.test.download.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class ExceptionController implements Initializable {

	@FXML
	private Label exceptionLbl;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}
	
	public void setExceptionMsg(String msg) {
		exceptionLbl.setText(msg);
	}
}
