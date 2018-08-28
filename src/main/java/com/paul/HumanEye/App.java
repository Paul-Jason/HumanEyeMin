package com.paul.HumanEye;

import org.opencv.core.Core;

import com.paul.HumanEye.TO.ResultTO;

public class App {
	public static void main(String[] arg){
		// load the native OpenCV library
		nu.pattern.OpenCV.loadShared();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		ResultTO resultDetails = new MatchTemplate().match("C:/Users/I329046/Documents/My Drive/Innovation/Human Eye/Images/test.png", "C:/Users/I329046/Documents/My Drive/Innovation/Human Eye/Images/Login.png");
        System.out.println("Matching data points are" + " (" +resultDetails.getMatchLocation().x + "," + resultDetails.getMatchLocation().y + ");");
	}
}