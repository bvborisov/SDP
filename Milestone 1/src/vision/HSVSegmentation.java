package src.vision;
import georegression.metric.UtilAngle;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
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


public class HSVSegmentation {
	private static BufferedImage segOutput;
	private static BufferedImage img;
	
	public static void main(String args[]) throws IOException{
		displayImage();
	}
	
	public static void displayImage() throws IOException{
		img = ImageIO.read(new File("img/00000002.jpg"));

		showSelectedColor("Marker(Y)", img, 0.7f, 0.95f);
		binaryOps("");
		showSelectedColor("Marker(B)", img, 3.0f, 0.8f);
		binaryOps("");
        showSelectedColor("Field", img, 2.0f, 0.55f);
        binaryOps("");
        showSelectedColor("Ball", img, 0, 1.1f);
        binaryOps("ball");
        showSelectedColor("Lines", img, 0.5f, 0.4f);
        binaryOps("");
	}
	
	public static void showSelectedColor( String name , BufferedImage image , float hue , float saturation ){
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
				segOutput = output;
				ShowImages.showWindow(output,"Segmented - "+name);
			}
	
	public static void binaryOps(String type) {
		
		MultiSpectral<ImageFloat32> input= ConvertBufferedImage.convertFromMulti(segOutput, null, true, ImageFloat32.class);
        ImageUInt8 binary = new ImageUInt8(input.width,input.height);
        ImageSInt32 label = new ImageSInt32(input.width,input.height);
        if (!type.equals("ball")){
        	ThresholdImageOps.threshold(input.getBand(1),binary,(float)50,false);
        }
        else {
        	ThresholdImageOps.threshold(input.getBand(0),binary,(float)100,false);
        }
        ImageUInt8 filtered = BinaryImageOps.erode8(binary,null);
        filtered = BinaryImageOps.dilate8(filtered, null);
        List<Contour> contours = BinaryImageOps.contour(filtered, 8, label);
        
        BufferedImage visualContour = VisualizeBinaryData.renderContours(contours,0xFFFFFF,0xFF2020,input.width,input.height,null);
        BufferedImage visualFiltered = VisualizeBinaryData.renderBinary(filtered, null);
        
        //ShowImages.showWindow(visualFiltered,"Filtered");
        ShowImages.showWindow(visualContour,"Contours");
        
			
	}
	
		 
		
}
	    


