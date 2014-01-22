package vision;
import au.edu.jcu.v4l4j.VideoDevice;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;


public class VideoTester {

	/**
	 * @param args
	 * @throws V4L4JException 
	 */
	public static void main(String[] args) throws V4L4JException {
		String vid = "/dev/video0";
		VideoDevice vd = new VideoDevice(vid);
	}

}