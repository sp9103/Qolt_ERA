#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <android/log.h>

#include <iostream>
#include <cstdio>
#include <stdio.h>
#include <string>

#include "Eye_Recovery.cpp"

using namespace cv;

extern "C" {
 // Eye_Recovorery class
	Eye_Recovery Era;

// Convert YUV420SP to ARGB
JNIEXPORT void JNICALL Java_libera_EraCore_decodeYUV420SP(JNIEnv* env, jobject obj, jbyteArray yuv420sp, jint width, jint height, jintArray rgbOut);

/*Open Data File, call this func -> real time convert start (one time)
	  - file open -> copy buffer -> file close*/
JNIEXPORT jint JNICALL Java_libera_EraCore_OpenDataFile(JNIEnv* env, jobject obj, jstring FilePath, jint mode);

/*Delete Data_Matrix. call this func => real time convert exit*/
JNIEXPORT void JNICALL Java_libera_EraCore_DeleteDataBuffer(JNIEnv* env, jobject obj);

/*image color weakness correction - Static Image*/
JNIEXPORT void JNICALL Java_libera_EraCore_RefineImage(JNIEnv* env, jobject obj, jlong src, jlong dst, jfloat factor);

/*image color weakness experience - Static Image*/
JNIEXPORT void JNICALL Java_libera_EraCore_InverseImage(JNIEnv* env, jobject obj, jlong src, jlong dst, jfloat factor);

/*Tree file Craete*/
JNIEXPORT jboolean JNICALL Java_libera_EraCore_MakeTreeFile(JNIEnv* env, jobject obj, jint interval, jfloat factor, jstring FilePath, jint mode);

/*Using Tree, Make Image*/
JNIEXPORT jint JNICALL Java_libera_EraCore_MakeImgtoData(JNIEnv* env, jobject obj, jlong src, jlong dst);

JNIEXPORT void JNICALL Java_libera_EraCore_decodeYUV420SP(JNIEnv* env, jobject obj, jbyteArray yuv420sp, jint width, jint height, jintArray rgbOut)
{
    int             size;
    int             i, j;
    int             Y;
    int             Cr = 0;
    int             Cb = 0;
    int             pixPtr = 0;
    int             jDiv2 = 0;
    int             R = 0;
    int             G = 0;
    int             B = 0;
    int             cOff;

    size = width * height;

    jint* rgbData = (jint*) env->GetPrimitiveArrayCritical(rgbOut, 0);
    jbyte* yuv = (jbyte*) env->GetPrimitiveArrayCritical(yuv420sp, 0);

    for(j = 0; j < height; j++) {
             pixPtr = j * width;
             jDiv2 = j >> 1;
             for(i = 0; i < width; i++) {
                     Y = yuv[pixPtr];
                     if(Y < 0) Y += 255;
                     if((i & 0x1) != 1) {
                             cOff = size + jDiv2 * width + (i >> 1) * 2;
                             Cb = yuv[cOff];
                             if(Cb < 0) Cb += 127; else Cb -= 128;
                             Cr = yuv[cOff + 1];
                             if(Cr < 0) Cr += 127; else Cr -= 128;
                     }
                     R = Y + Cr + (Cr >> 2) + (Cr >> 3) + (Cr >> 5);
                     if(R < 0) R = 0; else if(R > 255) R = 255;
                     G = Y - (Cb >> 2) + (Cb >> 4) + (Cb >> 5) - (Cr >> 1) + (Cr >> 3) + (Cr >> 4) + (Cr >> 5);
                     if(G < 0) G = 0; else if(G > 255) G = 255;
                     B = Y + Cb + (Cb >> 1) + (Cb >> 2) + (Cb >> 6);
                     if(B < 0) B = 0; else if(B > 255) B = 255;
                     rgbData[pixPtr++] = 0xff000000 + (B << 16) + (G << 8) + R;
             }
    }

    env->ReleasePrimitiveArrayCritical(rgbOut, rgbData, 0);
    env->ReleasePrimitiveArrayCritical(yuv420sp, yuv, 0);
}

JNIEXPORT jint JNICALL Java_libera_EraCore_OpenDataFile(JNIEnv* env, jobject obj, jstring FilePath, jint mode){
	//const char *nativeString = env->GetStringUTFChars(FilePath, 0);

	// use your string
	int return_value;

	switch(mode){
	case MODE_CORRECTION:
		return_value = Era.OpenDataFile("/sdcard/Pictures/ERA/ImgCorrectionData.bin");
		break;
	case MODE_DYSCHROMATOPSA:
		return_value = Era.OpenDataFile("/sdcard/Pictures/ERA/ImgDYSCHROMATOPSAData.bin");
		break;
	}

	//env->ReleaseStringUTFChars(FilePath, nativeString);

	return return_value;
}

JNIEXPORT void JNICALL Java_libera_EraCore_DeleteDataBuffer(JNIEnv* env, jobject obj){
	Era.DeleteDataBuffer();
}

JNIEXPORT void JNICALL Java_libera_EraCore_RefineImage(JNIEnv* env, jobject obj, jlong src, jlong dst, jfloat factor){
	
	Mat& t_src = *(Mat*)src;
	Mat& t_dst = *(Mat*)dst;

	Era.RefineImage(t_src, t_dst, factor);
}

JNIEXPORT void JNICALL Java_libera_EraCore_InverseImage(JNIEnv* env, jobject obj, jlong src, jlong dst, jfloat factor){
	Mat& t_src = *(Mat*)src;
	Mat& t_dst = *(Mat*)dst;

	Era.InverseImage(t_src, t_dst, factor);
}

JNIEXPORT jboolean JNICALL Java_libera_EraCore_MakeTreeFile(JNIEnv* env, jobject obj, jint interval, jfloat factor, jstring FilePath, jint mode){

	bool return_value;
	// use your string

	LOGI("Start File IO test");

	return_value = Era.MakeTreeFile(interval, factor, "empty_component", mode);

	LOGI("End Create DataFile");

	return return_value;
}

JNIEXPORT jint JNICALL Java_libera_EraCore_MakeImgtoData(JNIEnv* env, jobject obj, jlong src, jlong dst){
	Mat& t_src = *(Mat*)src;
	Mat& t_dst = *(Mat*)dst;

	//Mat resize_src = t_src.clone(), resize_dst = t_src.clone();

	//resize(resize_src, resize_src, Size(), 0.5, 0.5);
	resize(t_src, t_src, Size(), 0.4, 0.4);

	int return_Value = Era.MakeImage_to_Data(t_src, t_src);

	resize(t_src, t_dst, Size(), 2.5, 2.5);

	//resize(resize_dst, t_dst, Size(), 2, 2);

	//resize_src.release();
	//resize_dst.release();

	return return_Value;
}

}