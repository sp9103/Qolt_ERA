#include <math.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <opencv2/gpu/gpu.hpp>

#define MODE_CORRECTION 0
#define MODE_DYSCHROMATOPSA 1

#define MAX(a, b)	(((a)>(b)) ? (a) : (b))
#define MIN(a, b)	(((a)<(b)) ? (a) : (b))
#define UNDEFINED (0.0F)
#define CLIP(mid,low,high)		{if(mid<low)	mid=low;	if(mid>high)	mid=high;}