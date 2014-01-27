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
