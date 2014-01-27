package utililty;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

public class ImageProcessHelper {

	/**
	 * Convert bitmap to RGB_888 for Mat type CV processing
	 * 
	 * @param img bitmap image
	 * @return RGB_888 transformed bitmap image
	 */
	public static Bitmap JPEGtoRGB888(Bitmap img) {
		int numPixels = img.getWidth() * img.getHeight();
		int[] pixels = new int[numPixels];

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
	public static Bitmap CreateCompatibleBitmap(int width, int height){
		return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	}
	
	/**
	 * Converts given bitmap to OpenCV Mat type with CV_8UC4 option
	 * @param bitmap ARGB8888 Bitmap image
	 * @return Mat object
	 */
	public static Mat BitmapToMat(Bitmap bitmap){
		Mat mat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
		org.opencv.android.Utils.bitmapToMat(bitmap, mat);
		return mat;
	}
	
	/**
	 * Converts given Mat to Bitmap type
	 * @param mat object
	 * @return ARGB8888 Bitmap image
	 */
	public static Bitmap MatToBitmap(Mat mat){
		Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
		org.opencv.android.Utils.matToBitmap(mat, bitmap);
		return bitmap;
	}
	
	/**
	 * Returns the address of a Mat object for native codes
	 * @param mat
	 * @return pointer value to given Mat object (long)
	 */
	public static long MatPointer(Mat mat){
		return mat.nativeObj;
	}
}
