package vision;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.List;
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

        @Override
        public void nextFrame(VideoFrame frame) {
                // This method is called when a new frame is ready.
                // Don't forget to recycle it when done dealing with the frame.
                
        		long thisFrame = System.currentTimeMillis();
        		int frameRate = (int) (1000 / (thisFrame - lastFrame));
        		lastFrame = thisFrame;
        		
        		//System.out.println(frameRate);
        	
        		BufferedImage img = frame.getBufferedImage();
//                // draw the new frame onto the JLabel

        		//int[] window;// = new int[9];
        		int windowSize = 3;
        		
        		ArrayList<int[]> probCoords = new ArrayList<int[]>();
        		for(int i = 0; i < 639; i=i+3){
        			for(int j=0; j < 480; j=j+3){
        				
        				
        				int[] window = img.getRGB(i, j, 3, 3, null, 0, windowSize);
        				int redCount = 0;
        				
        				for(int k=0; k < windowSize*windowSize; k++){
        					Color C1 = new Color(window[k]);
        					
        					//color distance function, needs to be factored out
        					//ref - http://www.compuphase.com/cmetric.htm
        					int rmean = (C1.getRed() + 255)/2;
        					int red = C1.getRed() - 255;
        					int blue = C1.getBlue();
        					int green = C1.getGreen();
        					double distance = Math.sqrt((2.0 + rmean/256.0)*red*red + 4*green*green + (2 + (256 - rmean)/256.0)*blue*blue);
        					//double distance = Math.sqrt(Math.pow(red/red, 2)+Math.pow(green/red, 2)+Math.pow(blue/red, 2));
        					
        					
        					if (distance < 200){//some threshold needs tuning
        						//System.out.println("distance is " + distance);
        						redCount++;
        						//System.out.println(i + " " + j + " " +)
        					}
        					
        				}
        				if(redCount > 7){//threshold, needs tuning
        					
//        					System.out.println("I may have found the ball at " + i + " " + j + " red count is "
//        							+ redCount);
        					int[] tuple = {i,j};
        					probCoords.add(tuple);
        					
        				}
   
        			}
        		}
        		Graphics2D g = (Graphics2D) label.getGraphics();
                g.drawImage(img, 0, 0, width, height, null);
                if(probCoords.size() > 0){
                	
                	g.setColor(Color.PINK);
                	g.drawOval(
                		probCoords.get(0)[0], 
                		probCoords.get(0)[1], 
                		15,
                		15);
                }
                // recycle the frame
                frame.recycle();
        }
}