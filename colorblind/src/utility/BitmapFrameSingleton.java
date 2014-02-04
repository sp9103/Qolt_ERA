package utility;

import android.graphics.Bitmap;

public class BitmapFrameSingleton { 
		private Bitmap imageMat;
		private BitmapFrameSingleton(){
		}
		
		private static class SingletonHolder{
			static final BitmapFrameSingleton single = new BitmapFrameSingleton();
		}
		
		public static BitmapFrameSingleton getInstance(){
			return SingletonHolder.single;
		}
		
		public void setImage(Bitmap imgSelection){
			this.imageMat = imgSelection;
		}
		
		public Bitmap getImage(){
			return this.imageMat;
		}
		
		public void freeImage(){
			this.imageMat.recycle();
		}
	}