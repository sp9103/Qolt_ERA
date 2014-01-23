package libera;

public class EraCore {
	public EraCore(){
		try{
			System.loadLibrary("ERA");
		}
		catch(OutOfMemoryError e){
			e.printStackTrace();
		}
	}
	/**
	 *  JAVA library method (one you would call in the actual project activity)
	 * @param yuv  input byte array
	 * @param width image pixel width
	 * @param height image pixel height
	 * @param rgb   output int array
	 */
	public void decodeYUVtoRGB(byte[] yuv, int width, int height, int[] rgb){
		decodeYUV420SP(yuv, width, height, rgb);
	}
	
	/*
	 * Native functions to be called from the zxJNI library 
	 */
	public native void decodeYUV420SP(byte[] yuv420sp, int width, int height, int[] rgbOut);
	
}
