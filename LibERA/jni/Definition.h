#include <math.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <jni.h>

#include <iostream>
#include <cstdio>
#include <stdio.h>
#include <string>

#define LOG_TAG "JNI"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

#define MODE_CORRECTION 0
#define MODE_DYSCHROMATOPSA 1

#define MAX(a, b)	(((a)>(b)) ? (a) : (b))
#define MIN(a, b)	(((a)<(b)) ? (a) : (b))
#define UNDEFINED (0.0F)
#define CLIP(mid,low,high)		{if(mid<low)	mid=low;	if(mid>high)	mid=high;}