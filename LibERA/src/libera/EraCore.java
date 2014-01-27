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
	
	
	/*
	 * Native functions to be called from the JNI library 
	 */
	
	/* decode YUV420SP to RGB bitmap array */
	public static native void decodeYUV420SP(byte[] yuv420sp, int width, int height, int[] rgbOut);
	
	///////////////////////////////////////////////////Image Process ERA Func//////////////////////////////////////////////////////////////////////
	
	/*Open Data File, call this func -> real time convert start (one time)
	  - file open -> copy buffer -> file close*/
	public static native void OpenDataFile(String FilePath);
	/*Delete Data_Matrix. call this func => real time convert exit*/
	public static native int DeleteDataBuffer();
	/*image color weakness correction - Static Image*/
	public static native void RefineImage(long src, long dst, float factor);
	/*image color weakness experience - Static Image*/
	public static native void InverseImage(long src, long dst, float factor);
	/*Tree file Craete*/
	public static native boolean MakeTreeFile(int interval, float factor, String FilePath, int mode);
	/*Using Tree, Make Image*/
	public static native int MakeImgtoData(long src, long dst);

	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
