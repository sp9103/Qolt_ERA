package utility;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.util.Log;

public class ImageProcessHelper {

	public ImageProcessHelper(){
		if(!OpenCVLoader.initDebug())
	    	Log.d("CVerror","OpenCV library Init failure");
	}
	/**
	 * Convert bitmap to RGB_888 for Mat type CV processing
	 * 
	 * @param img bitmap image
	 * @return RGB_888 transformed bitmap image
	 */
	public Bitmap JPEGtoRGB888(Bitmap img) {
		int numPixels = img.getWidth() * img.getHeight();
		int[] pixels = new int[numPixels];
		Log.i("TEST", "size of array :"+numPixels);
		// Get JPEG pixel. single int per pixel
		img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(),
				img.getHeight());

		// Create ARGB888 bitmap
		Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(),
				Config.ARGB_8888);

		// put values into the new bitmap
		result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(),
				result.getHeight());
		
		return result;
	}
	
	/**
	 * Creates new ARGB_8888 bitmap
	 * 
	 * @param target img width
	 * @param target img height
	 * @return returns OpenCV Mat type compatible Bitmap
	 */
	public Bitmap CreateCompatibleBitmap(int width, int height){
		return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	}
	
	/**
	 * Converts given bitmap to OpenCV Mat type with CV_8UC4 option
	 * @param bitmap ARGB8888 Bitmap image
	 * @return Mat object
	 */
	public Mat BitmapToMat(Bitmap bitmap){
		
		Mat mat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
		org.opencv.android.Utils.bitmapToMat(bitmap, mat);
		return mat;

	}
	
	/**
	 * Converts given Mat to Bitmap type
	 * @param mat object
	 * @return ARGB8888 Bitmap image
	 */
	public Bitmap MatToBitmap(Mat mat){
		
		Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
		org.opencv.android.Utils.matToBitmap(mat, bitmap);
		return bitmap;

		
	}
}
