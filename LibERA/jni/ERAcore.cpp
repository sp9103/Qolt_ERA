#include <jni.h>
#include <android/log.h>

#define LOG_TAG "JNI"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

// Convert YUV420SP to ARGB
JNIEXPORT void JNICALL Java_libera_EraCore_decodeYUV420SP(JNIEnv* env, jobject obj, jbyteArray yuv420sp, jint width, jint height, jintArray rgbOut);

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
