package com.paul.HumanEye;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.paul.HumanEye.TO.ResultTO;

class MatchTemplate {
    Mat img = new Mat(), templ = new Mat();
    int match_method;
    
    ResultTO resultDetails = new ResultTO();
    public ResultTO match(String pathToBigImage, String pathToSmallImage) {
        img = Imgcodecs.imread(pathToBigImage, Imgcodecs.IMREAD_COLOR);
        templ = Imgcodecs.imread(pathToSmallImage, Imgcodecs.IMREAD_COLOR);
        matchingMethod();
        return resultDetails;
    }
    
    private void matchingMethod() {
        Mat result = new Mat();
        Mat img_display = new Mat();
        img.copyTo(img_display);
        int result_cols = img.cols() - templ.cols() + 1;
        int result_rows = img.rows() - templ.rows() + 1;
        result.create(result_rows, result_cols, CvType.CV_32FC1);
            Imgproc.matchTemplate(img, templ, result, match_method);
        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
        Point matchLoc;
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        if (match_method == Imgproc.TM_SQDIFF || match_method == Imgproc.TM_SQDIFF_NORMED) {
            matchLoc = mmr.minLoc;
        } else {
            matchLoc = mmr.maxLoc;
        }
        resultDetails.setMatchLocation(matchLoc);
        resultDetails.setTemplRows(templ.rows());
        resultDetails.setTemplCols(templ.cols());
    }
}