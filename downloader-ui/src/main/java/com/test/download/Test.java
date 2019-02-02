package com.test.download;

import com.gluonhq.charm.glisten.control.ProgressBar;

public class Test {
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ProgressBar getPb() {
		return pb;
	}
	public void setPb(ProgressBar pb) {
		this.pb = pb;
	}
	private ProgressBar pb;

}
