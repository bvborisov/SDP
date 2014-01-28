package src.vision;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

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
        public static ArrayList<int[]> findBallrgb(
        		BufferedImage img, 
        		int windowSize, 
        		int windowTolerance,
        		double redThreshold)
        {
        	ArrayList<int[]> probCoords = new ArrayList<int[]>();
        	int width = img.getWidth();
        	int height = img.getHeight();
        	for(int i = 0; i < width; i=i+windowSize){ 
        		for(int j=0; j < height; j=j+windowSize){
        			int[] window = img.getRGB(i, j, windowSize, windowSize, null, 0, windowSize); // get rgb values, stores as ints for some reason...
        			int redCount = 0; 
        			for(int k=0; k < windowSize*windowSize; k++){
        				Color C1 = new Color(window[k]); 
        				double r = (double)C1.getRed() / (C1.getRed() + C1.getBlue() + C1.getGreen());
        				if(r > redThreshold){
        					redCount++;
        				}
        			}
        			if( redCount > windowTolerance ){// arbitrary threshold, needs tuning
        				int[] tuple = {i,j};
        				probCoords.add(tuple);
        			}
        		}
        	}
        	return probCoords;
        }
        /*
         * Finds the ball using true RGB colors
         */
        public static ArrayList<int[]> findBallRGB(
        		BufferedImage img, 
        		int windowSize, 
        		int windowTolerance,
        		int distanceThreshold){
        	ArrayList<int[]> probCoords = new ArrayList<int[]>();
        	int width = img.getWidth();
        	int height = img.getHeight();
        	for(int i = 0; i < width; i=i+windowSize){ 
        		for(int j=0; j < height; j=j+windowSize){
        			int[] window = img.getRGB(i, j, windowSize, windowSize, null, 0, windowSize); // get rgb values, stores as ints for some reason...
        			int redCount = 0; 
        			for(int k=0; k < windowSize*windowSize; k++){
        				Color C1 = new Color(window[k]); 
        				Color C2 = Color.RED;

        				int rmean = (C1.getRed() + C2.getRed())/2; // the 255 here comes from implicit True Red
        				int red = C1.getRed() - C2.getRed();
        				int blue = C1.getBlue() - C2.getBlue();
        				int green = C1.getGreen() - C2.getGreen();
        				
        				//reference - http://www.compuphase.com/cmetric.htm
        				double distance = Math.sqrt((2.0 + rmean/256.0)*red*red + 4*green*green + (2 + (256 - rmean)/256.0)*blue*blue);
        				if (distance < distanceThreshold){// some arbitrary threshold needs fine tuning, rough tuning ok
        					redCount++;
        					//TODO 1: ignore yellow
        				}
        			}
        			if( redCount > windowTolerance ){// arbitrary threshold, needs tuning
        				int[] tuple = {i,j};
        				probCoords.add(tuple);
        			}
        		}
        	}
        	return probCoords;
        }
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
                
            /* Vision code:
                We swipe a windowSize x windowSize window across the image
                and for each window we compute how many of the pixels inside of it
                are "close" to true red(255,0,0) by using some heuristic euclidean distance
                based RGB function, see link for more info.

                If the count of the number of pixels is more than 7 we push the coordinates
                of the window to a list of possible coords

                Finally we select the first coordinate pair from the list and we
                draw an oval arround that point - it should represent the rough 
                position of the ball.

                TODO: 
                1. check threshhold for distance function, make it ignore Yellow
                2. somehow use other probable coordinate pairs
                3. we report top right coordinate of a window, should it be something else though?
                4. replace all values of 3 with windowSize
                5. factor out color distance function if needed someplace else
             */
        	int windowSize = 5; //arbitrary
        	ArrayList<int[]> probCoords = findBallrgb(img, windowSize, 7*windowSize*windowSize/9, 0.70);
        	//ArrayList<int[]> probCoords = findBallRGB(img, windowSize, 7*windowSize*windowSize/9, 200);
        	Graphics2D g = (Graphics2D) label.getGraphics();
        	// this draws the frame grabber
            g.drawImage(img, 0, 0, width, height, null);
            if(probCoords.size() > 0){
            	// DEBUG code
            	for(int[] x: probCoords){
            		System.out.print(x[0] + " " + x[1] + "| ");
            		System.out.print(new Color(img.getRGB(x[0], x[1])));
            	}
        		System.out.println("");
            	// draws a cross over the ball
                g.setColor(Color.PINK);
                g.drawLine(probCoords.get(0)[0] - 15 , probCoords.get(0)[1], probCoords.get(0)[0] + 15 , probCoords.get(0)[1]);
                g.drawLine(probCoords.get(0)[0] , probCoords.get(0)[1] - 15, probCoords.get(0)[0], probCoords.get(0)[1] + 15);

            }
            // recycle the frame
            frame.recycle();
        }
}
