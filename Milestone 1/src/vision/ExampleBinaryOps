package vision;
/*
 * Copyright (c) 2011-2013, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 
 
 */




import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.alg.filter.binary.ThresholdImageOps;
import boofcv.alg.misc.ImageStatistics;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.gui.binary.VisualizeBinaryData;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSInt32;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.MultiSpectral;
import georegression.struct.point.Point2D_I32;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Demonstrates how to create binary images by thresholding, applying binary morphological operations, and
 * then extracting detected features by finding their contours.
 *
 * This class computes binary thresholding of an image in its red bad
 * filters and gets the contours of the remaining blobs  and
 * xMean and yMean are the mean of all the points of the contour
 * TODO : comment and factor out important code
 */
public class ExampleBinaryOps {

        public static void main( String args[] ) {
                // load and convert the image into a usable format
        		BufferedImage image = UtilImageIO.loadImage("img1.jpg");

                // convert into a usable format
                ImageFloat32 input = ConvertBufferedImage.convertFromSingle(image, null, ImageFloat32.class);
                ImageUInt8 binary = new ImageUInt8(input.width,input.height);
                ImageSInt32 label = new ImageSInt32(input.width,input.height);

                // the mean pixel value is often a reasonable threshold when creating a binary image
                double mean = ImageStatistics.mean(input);

                // create a binary image by thresholding
                ThresholdImageOps.threshold(input,binary,(float)mean,true);

                // remove small blobs through erosion and dilation
                // The null in the input indicates that it should internally declare the work image it needs
                // this is less efficient, but easier to code.
                ImageUInt8 filtered = BinaryImageOps.erode8(binary,null);
                filtered = BinaryImageOps.dilate8(filtered, null);

                // Detect blobs inside the image using an 8-connect rule
                List<Contour> contours = BinaryImageOps.contour(filtered, 8, label);

                // colors of contours
                int colorExternal = 0xFFFFFF;
                int colorInternal = 0xFF2020;

                // display the results
                BufferedImage visualBinary = VisualizeBinaryData.renderBinary(binary, null);
                BufferedImage visualFiltered = VisualizeBinaryData.renderBinary(filtered, null);
                BufferedImage visualLabel = VisualizeBinaryData.renderLabeled(label, contours.size(), null);
                BufferedImage visualContour = VisualizeBinaryData.renderContours(contours,colorExternal,colorInternal,
                                input.width,input.height,null);

                //ShowImages.showWindow(visualBinary,"Binary Original");
                //ShowImages.showWindow(visualFiltered,"Binary Filtered");
                //ShowImages.showWindow(visualLabel,"Labeled Blobs");
                //ShowImages.showWindow(visualContour,"Contours");
                
                // my code for the Ball attention the Ball
                BufferedImage image2 = UtilImageIO.loadImage("frame1.jpg");
                
                MultiSpectral<ImageFloat32> input2= ConvertBufferedImage.convertFromMulti(image2, null, true, ImageFloat32.class);
                ImageUInt8 binary2 = new ImageUInt8(input2.width,input2.height);
                ImageSInt32 label2 = new ImageSInt32(input2.width,input2.height);
                
                // create a binary image by thresholding
                System.out.println(input2.getBand(0).getWidth());
                System.out.println(input2.getBand(0).getHeight());
                
                float redThreshold = 190; // 100 segments field, 190 finds ball
                float greenThreshold = 50; // 
             
                ThresholdImageOps.threshold(input2.getBand(0),binary2,(float)redThreshold,false);
                //ThresholdImageOps.threshold(input2.getBand(1),binary2,(float)greenThreshold,false);
                
                ImageUInt8 filtered2 = BinaryImageOps.erode8(binary2,null);
                filtered2 = BinaryImageOps.dilate8(filtered2, null);
                
                // Detect blobs inside the image using an 8-connect rule
                List<Contour> contours2 = BinaryImageOps.contour(filtered2, 8, label2);
                
                int xMean = 0;
                int yMean = 0;
                for(Point2D_I32 p: contours2.get(0).external){
                	xMean += p.x;
                	yMean += p.y;
                }
                xMean /= contours2.get(0).external.size();
                yMean /= contours2.get(0).external.size();	
                
                System.out.println("xMean " + xMean + " " + yMean );
                
                // colors of contours
                int colorExternal2 = 0xFFFFFF;
                int colorInternal2 = 0xFF2020;
                // visualise
                BufferedImage visualBinary2 = VisualizeBinaryData.renderBinary(binary2, null);
                BufferedImage visualFiltered2 = VisualizeBinaryData.renderBinary(filtered2, null);
                BufferedImage visualLabel2 = VisualizeBinaryData.renderLabeled(label2, contours2.size(), null);
                BufferedImage visualContour2 = VisualizeBinaryData.renderContours(contours2,colorExternal2,colorInternal2,
                                input2.width,input2.height,null);
                
                
                ShowImages.showWindow(visualBinary2,"Binary Original");
                ShowImages.showWindow(visualFiltered2,"Binary Filtered");
                ShowImages.showWindow(visualLabel2,"Labeled Blobs");
                ShowImages.showWindow(visualContour2,"Contours");
                
                
        }

}
