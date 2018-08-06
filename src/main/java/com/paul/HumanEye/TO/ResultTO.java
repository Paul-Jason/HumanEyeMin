package com.paul.HumanEye.TO;

import org.opencv.core.Point;

public class ResultTO {
	
	private Point matchLocation;
	
	private int templRows;
	
	private int templCols;
	
	public ResultTO(){
		
	}
	
	public ResultTO(Point matchLocation, int templRows, int templCols){
		this.matchLocation = matchLocation;
		this.templRows = templRows;
		this.templCols = templCols;
	}

	public Point getMatchLocation() {
		return matchLocation;
	}

	public void setMatchLocation(Point matchLocation) {
		this.matchLocation = matchLocation;
	}

	public int getTemplRows() {
		return templRows;
	}

	public void setTemplRows(int templRows) {
		this.templRows = templRows;
	}

	public int getTemplCols() {
		return templCols;
	}

	public void setTemplCols(int templCols) {
		this.templCols = templCols;
	}

}
