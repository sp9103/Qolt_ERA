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
	
	///////////////////////////////////////////////////Image Process ERA Func//////////////////////////////////////////////////////////////////////
	
	/*Open Data File, call this func -> real time convert start (one time)
	  - file open -> copy buffer -> file close*/
	public native void OpenDataFile(String FilePath);
	/*Delete Data_Matrix. call this func => real time convert exit*/
	public native int DeleteDataBuffer();
	/*image color weakness correction - Static Image*/
	public native void RefineImage(long src, long dst, float factor);
	/*image color weakness experience - Static Image*/
	public native void InverseImage(long src, long dst, float factor);
	/*Tree file Craete*/
	public native boolean MakeTreeFile(int interval, float factor, String FilePath, int mode);
	/*Using Tree, Make Image*/
	public native int MakeImgtoData(long src, long dst);

	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
