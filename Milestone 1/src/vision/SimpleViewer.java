package src.vision;

import georegression.metric.UtilAngle;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import boofcv.alg.color.ColorHsv;
import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.alg.filter.binary.ThresholdImageOps;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.gui.binary.VisualizeBinaryData;
import boofcv.gui.image.ShowImages;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSInt32;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.MultiSpectral;

import au.edu.jcu.v4l4j.FrameGrabber;
import au.edu.jcu.v4l4j.CaptureCallback;
import au.edu.jcu.v4l4j.V4L4JConstants;
import au.edu.jcu.v4l4j.VideoDevice;
import au.edu.jcu.v4l4j.VideoFrame;
import au.edu.jcu.v4l4j.exceptions.StateException;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;

/**
 * This class demonstrates how to perform a simple push-mode capture.
 * It starts the capture and display the video stream in a JLabel
 * @author gilles
 *
 */
public class SimpleViewer extends WindowAdapter implements CaptureCallback{
        private static int      width = 640, height = 480, std = V4L4JConstants.STANDARD_WEBCAM, channel = 0;
        private static String   device = "/dev/video0";
        private long lastFrame = System.currentTimeMillis(); // used for calculating FPS
        private int frameCounter = 0; // we let device capture frames from the 3rd onwards
        
        private VideoDevice     videoDevice;
        private FrameGrabber    frameGrabber;

        private JLabel          label;
        private JFrame          frame;
        private static BufferedImage segOutputBall;



        public static void main(String args[]){

                SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                                new SimpleViewer();
                        }
                });
        }

        /**
         * Builds a WebcamViewer object
         * @throws V4L4JException if any parameter if invalid
         */
        public SimpleViewer(){
                // Initialise video device and frame grabber
                try {
                        initFrameGrabber();
                } catch (V4L4JException e1) {
                        System.err.println("Error setting up capture");
                        e1.printStackTrace();
                        
                        // cleanup and exit
                        cleanupCapture();
                        return;
                }
                
                // create and initialise UI
                initGUI();
                
                // start capture
                try {
                        frameGrabber.startCapture();
                } catch (V4L4JException e){
                        System.err.println("Error starting the capture");
                        e.printStackTrace();
                }
        }

        /**
         * Initialises the FrameGrabber object
         * @throws V4L4JException if any parameter if invalid
         */
        private void initFrameGrabber() throws V4L4JException{
                videoDevice = new VideoDevice(device);
                frameGrabber = videoDevice.getJPEGFrameGrabber(width, height, channel, std, 80);
                frameGrabber.setCaptureCallback(this);
                width = frameGrabber.getWidth();
                height = frameGrabber.getHeight();
                System.out.println("Starting capture at "+width+"x"+height);
        }

        /** 
         * Creates the UI components and initialises them
         */
        private void initGUI(){
                frame = new JFrame();
                label = new JLabel();
                frame.getContentPane().add(label);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.addWindowListener(this);
                frame.setVisible(true);
                frame.setSize(width, height);
        }
        
        /**
         * this method stops the capture and releases the frame grabber and video device
         */
        private void cleanupCapture() {
                try {
                        frameGrabber.stopCapture();
                } catch (StateException ex) {
                        // the frame grabber may be already stopped, so we just ignore
                        // any exception and simply continue.
                }

                // release the frame grabber and video device
                videoDevice.releaseFrameGrabber();
                videoDevice.release();
        }

        /**
         * Catch window closing event so we can free up resources before exiting
         * @param e
         */
        public void windowClosing(WindowEvent e) {
                cleanupCapture();

                // close window
                frame.dispose();            
        }


        @Override
        public void exceptionReceived(V4L4JException e) {
                // This method is called by v4l4j if an exception
                // occurs while waiting for a new frame to be ready.
                // The exception is available through e.getCause()
                e.printStackTrace();
        }
        /*
         * 	Finds the ball using normalised RGB colors.
         * 
         */
        
        /*
        This method contains all the current vision code and the frame handling code.
        The frame handling code is quite simple - it just fetches the frame from
        the video device and writes it to the JPabel window.
        
        The vision code is marked down and commented
        */
        @Override
        public void nextFrame(VideoFrame frame) {
                // This method is called when a new frame is ready.
                // Don't forget to recycle it when done dealing with the frame.
        	
            BufferedImage img = frame.getBufferedImage();
            //img = showSelectedColor("Ball", img, 0, 0.8f);
            img = contourOps("field", img);
            //img = showSelectedColor("Lines", img, 0.5f, 0.4f);
            //img = showSelectedColor("Field", img, 2.0f, 0.55f);
            Graphics2D g = (Graphics2D) label.getGraphics();	 	
            // this draws the frame grabber	 	
            g.drawImage(img, 0, 0, width, height, null);
            
            // recycle the frame
            frame.recycle();
        }
        
        public BufferedImage showSelectedColor( String name , BufferedImage image , float hue , float saturation ){
        	MultiSpectral<ImageFloat32> input = ConvertBufferedImage.convertFromMulti(image,null,true,ImageFloat32.class);
    		MultiSpectral<ImageFloat32> hsv = new MultiSpectral<ImageFloat32>(ImageFloat32.class,input.width,input.height,3);
    		
    		// Convert into HSV
    				ColorHsv.rgbToHsv_F32(input,hsv);
    		 
    				// Pixels which are more than this different from the selected color are set to black
    				float maxDist2 = 0.4f*0.4f;
    		 
    				// Extract hue and saturation bands which are independent of intensity
    				ImageFloat32 H = hsv.getBand(0);
    				ImageFloat32 S = hsv.getBand(1);
    		 
    				// Adjust the relative importance of Hue and Saturation
    				float adjustUnits = (float)(Math.PI/2.0);
    		 
    				// step through each pixel and mark how close it is to the selected color
    				BufferedImage output = new BufferedImage(input.width,input.height,BufferedImage.TYPE_INT_RGB);
    				for( int y = 0; y < hsv.height; y++ ) {
    					for( int x = 0; x < hsv.width; x++ ) {
    						// remember Hue is an angle in radians, so simple subtraction doesn't work
    						float dh = UtilAngle.dist(H.unsafe_get(x,y),hue);
    						float ds = (S.unsafe_get(x,y)-saturation)*adjustUnits;
    		 
    						// this distance measure is a bit naive, but good enough for this demonstration
    						float dist2 = dh*dh + ds*ds;
    						if( dist2 <= maxDist2 ) {
    							output.setRGB(x,y,image.getRGB(x,y));
    						}
    					}
    				}
    				return output;
        }
        
        public BufferedImage contourOps(String type, BufferedImage inputImg) {
    		
    		MultiSpectral<ImageFloat32> input= ConvertBufferedImage.convertFromMulti(inputImg, null, true, ImageFloat32.class);
            ImageUInt8 binary = new ImageUInt8(input.width,input.height);
            ImageSInt32 label = new ImageSInt32(input.width,input.height);
            if (type.equals("ball")){
            	ThresholdImageOps.threshold(input.getBand(0),binary,(float)180,false);
            }
            else if (type.equals("field")) {
            	ThresholdImageOps.threshold(input.getBand(0),binary,(float)100,false);
            }
            ImageUInt8 filtered = BinaryImageOps.erode8(binary,null);
            filtered = BinaryImageOps.dilate8(filtered, null);
            List<Contour> contours = BinaryImageOps.contour(filtered, 8, label);
            
            BufferedImage visualContour = VisualizeBinaryData.renderContours(contours,0xFFFFFF,0xFF2020,input.width,input.height,null);
            
            return visualContour;
    	}
}
