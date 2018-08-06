package com.paul.HumanEye;

import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.paul.HumanEye.TO.ResultTO;

class MatchTemplate implements ChangeListener {
    Boolean use_mask = false;
    Mat img = new Mat(), templ = new Mat();
    Mat mask = new Mat();
    int match_method;
    JLabel imgDisplay = new JLabel(), resultDisplay = new JLabel();
    
    ResultTO resultDetails = new ResultTO();
    public ResultTO match(String pathToBigImage, String pathToSmallImage) {
    	
        img = Imgcodecs.imread(pathToBigImage, Imgcodecs.IMREAD_COLOR);
        templ = Imgcodecs.imread(pathToSmallImage, Imgcodecs.IMREAD_COLOR);
//        if (args.length > 2) {
//            use_mask = true;
//            mask = Imgcodecs.imread(args[2], Imgcodecs.IMREAD_COLOR);
//        }
        if (img.empty() || templ.empty() || (use_mask && mask.empty())) {
            System.out.println("Can't read one of the images");
            System.exit(-1);
        }
        matchingMethod();
        createJFrame();
        return resultDetails;
    }
    private void matchingMethod() {
        Mat result = new Mat();
        Mat img_display = new Mat();
        img.copyTo(img_display);
        int result_cols = img.cols() - templ.cols() + 1;
        int result_rows = img.rows() - templ.rows() + 1;
        result.create(result_rows, result_cols, CvType.CV_32FC1);
        Boolean method_accepts_mask = (Imgproc.TM_SQDIFF == match_method || match_method == Imgproc.TM_CCORR_NORMED);
        if (use_mask && method_accepts_mask) {
            Imgproc.matchTemplate(img, templ, result, match_method, mask);
        } else {
            Imgproc.matchTemplate(img, templ, result, match_method);
        }
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
        Imgproc.rectangle(img_display, matchLoc, new Point(matchLoc.x + templ.cols(), matchLoc.y + templ.rows()),
                new Scalar(0, 0, 0), 2, 8, 0);
        Imgproc.rectangle(result, matchLoc, new Point(matchLoc.x + templ.cols(), matchLoc.y + templ.rows()),
                new Scalar(0, 0, 0), 2, 8, 0);
        Image tmpImg = toBufferedImage(img_display);
        ImageIcon icon = new ImageIcon(tmpImg);
        imgDisplay.setIcon(icon);
        result.convertTo(result, CvType.CV_8UC1, 255.0);
        tmpImg = toBufferedImage(result);
        icon = new ImageIcon(tmpImg);
        resultDisplay.setIcon(icon);
    }
    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!source.getValueIsAdjusting()) {
            match_method = source.getValue();
            matchingMethod();
        }
    }
    private void createJFrame() {
        String title = "Source image; Control; Result image";
        JFrame frame = new JFrame(title);
        frame.setLayout(new GridLayout(2, 2));
        frame.add(imgDisplay);
        int min = 0, max = 5;
        JSlider slider = new JSlider(JSlider.VERTICAL, min, max, match_method);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        // Set the spacing for the minor tick mark
        slider.setMinorTickSpacing(1);
        // Customizing the labels
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(new Integer(0), new JLabel("0 - SQDIFF"));
        labelTable.put(new Integer(1), new JLabel("1 - SQDIFF NORMED"));
        labelTable.put(new Integer(2), new JLabel("2 - TM CCORR"));
        labelTable.put(new Integer(3), new JLabel("3 - TM CCORR NORMED"));
        labelTable.put(new Integer(4), new JLabel("4 - TM COEFF"));
        labelTable.put(new Integer(5), new JLabel("5 - TM COEFF NORMED : (Method)"));
        slider.setLabelTable(labelTable);
        slider.addChangeListener(this);
        frame.add(slider);
        frame.add(resultDisplay);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    //Convert a mat image to buffered image.
    private BufferedImage toBufferedImage(Mat matrix){
    	BufferedImage bimg = null;
    	if ( matrix != null ) { 
            int cols = matrix.cols();  
            int rows = matrix.rows();  
            int elemSize = (int)matrix.elemSize();  
            byte[] data = new byte[cols * rows * elemSize];  
            int type;  
            matrix.get(0, 0, data);  
            switch (matrix.channels()) {  
            case 1:  
                type = BufferedImage.TYPE_BYTE_GRAY;  
                break;  
            case 3:  
                type = BufferedImage.TYPE_3BYTE_BGR;  
                // bgr to rgb  
                byte b;  
                for(int i=0; i<data.length; i=i+3) {  
                    b = data[i];  
                    data[i] = data[i+2];  
                    data[i+2] = b;  
                }  
                break;  
            default:  
                return null;  
            }  
            // Reuse existing BufferedImage if possible
            if (bimg == null || bimg.getWidth() != cols || bimg.getHeight() != rows || bimg.getType() != type) {
                bimg = new BufferedImage(cols, rows, type);
            }        
            bimg.getRaster().setDataElements(0, 0, cols, rows, data);
        } else { // mat was null
            bimg = null;
        }
        return bimg;  
    }
}