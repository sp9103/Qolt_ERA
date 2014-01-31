#include <stdio.h>
#include <windows.h>
#include <cv.h>
#include <highgui.h>

#define MODE_CORRECTION 0
#define MODE_DYSCHROMATOPSA 1

#define CLIP(mid,low,high)		{if(mid<low)	mid=low;	if(mid>high)	mid=high;}

float modification_factor = 1.0f;
float inverse_factor = 1.0f;
int temp_factor = 100;
int t_inverse_factor = 100;


void RGB_To_HSI(float	r, float g, float b, float *h, float *s, float *i)
{
	float minc;              /// minimum and maximum RGB values 
	float angle;             /// temp variable used to compute Hue 

	minc = MIN(r,g);
	minc = MIN(minc,b);

	/// compute intensity 
	*i=(r + g + b) / 3.0f;

	/// compute hue and saturation 
	if((r==g) && (g==b))  /// gray-scale 
	{
		*s = 0.0f;
		*h = 0.0f;
		return;
	}
	else
	{
		*s= 1.0f - (3.0f / (r + g + b)) * minc;
		angle = (r - 0.5f * g - 0.5f * b) / (float)sqrt((r - g) * (r - g)+(r - b) * (g - b));
		*h = (float)acos(angle);
		*h *= 57.29577951f;          /// convert to degrees 
	}

	if(b>g)	*h = 360.0f - *h;
}

void HSI_To_RGB(float h, float s, float i, float *r, float *g, float *b)
{
	float angle1, angle2, scale;   /// temp variables 

	if(i==0.0)    /// BLACK 
	{
		*r = 0.0f;
		*g = 0.0f;
		*b = 0.0f;
		return;
	}
	if(s==0.0)     /// gray-scale  H is undefined
	{
		*r = i;
		*g = i;
		*b = i;
		return;
	}
	if(h<0.0)	h += 360.0f;

	scale = 3.0f * i;
	if(h<=120.0)
	{
		angle1=h*0.017453293f;    /// convert to radians - mul by pi/180 
		angle2=(60.0f-h)*0.017453293f;

		*b = (1.0f-s)/3.0f;
		*r = (float)(1.0f + (s*cos(angle1)/cos(angle2)))/3.0f;
		*g = 1.0f-*r-*b;
		*b *= scale;
		*r *= scale;
		*g *= scale;
	}
	else if((h>120.0) && (h<=240.0))
	{
		h -= 120.0f;
		angle1=h*0.017453293f;    /// convert to radians - mul by pi/180 
		angle2=(60.0f-h)*0.017453293f;

		*r = (1.0f-s)/3.0f;
		*g = (float)(1.0f + (s*cos(angle1)/cos(angle2)))/3.0f;
		*b = 1.0f - *r - *g;
		*r *= scale;
		*g *= scale;
		*b *= scale;
	}
	else
	{
		if(h==0)	h=360.0f;
		h -= 240.0f;
		angle1=h*0.017453293f;    /// convert to radians - mul by pi/180 
		angle2=(60.0f-h)*0.017453293f;

		*g = (1.0f-s)/3.0f;
		*b = (float)(1.0 + (s*cos(angle1)/cos(angle2)))/3.0f;
		*r = 1.0f - *g - *b;
		*r *= scale;
		*g *= scale;
		*b *= scale;
	}
}

void HSI_Revision(float *h, float *s, float *l, float modification_factor){
	if(modification_factor < 0.4){
		if(330.0f < *h && *h < 360.0f){
			*h = *h - 330.0f;
		}else if(0.0f < *h && *h < 60 * (1 * modification_factor)){
			*h = 300 - *h;
		}else if(120.0f - 60.0f < *h && *h < 120.0f){
			*h = 300 - *h;
		}else if(120.0f < *h && *h < 180.0f){
			*h = 150 - *h;
		}
	}else{
		if(60.0f - 90.0f * modification_factor < 0.0f)
		{
			if(*h >= 60.0f * (1 - modification_factor) && *h <= 60.0f + 90.0f * modification_factor)          *h = (*h - 60.0f) / modification_factor + 60.0f;
			else if(*h >= 240.0f - 90.0f * modification_factor && *h <= 240.0f + 90.0f * modification_factor)  *h = (*h - 240.0f) / modification_factor + 240.0f;
			else if(*h > 420.0f - 90 * modification_factor)                               *h = (*h - 420.0f) / modification_factor + 420.0f;
			else if(*h >= 0.0f && *h < 60.0f - 60.0f * modification_factor)	             *h = (*h - 60.0f) / modification_factor + 420.0f;
		}
		else
		{
			if(*h >= 60.0f * (1 - modification_factor) && *h <= 60.0f + 90.0f * modification_factor)          *h = (*h - 60.0f) / modification_factor + 60.0f;
			else if(*h >= 240.0f - 90.0f * modification_factor && *h <= 240.0f + 90.0f * modification_factor)  *h = (*h - 240.0f) / modification_factor + 240.0f;
			else if(*h >= 60.0f - 90.0f * modification_factor && *h < 60.0f - 60.0f * modification_factor)     *h = (*h - 60.0f) / modification_factor + 420.0f;
		}
	}
}

void Dyschromatopsa(float *h, float *s, float *l, float modification_factor){
	float hue2, sss;

	if((*h>=0.0f && *h<=150.0f) || (330.0f<=*h && *h<=360.0f))
	{
		if(*h>=0.0f && *h<=150.0f)	*h=60.0f+(*h-60.0f)*(float)modification_factor/*/1.4*/;
		else
		{
			hue2=60-(60.0f+(360.0f-*h))*(float)modification_factor/*/1.4*/;
			if(hue2<0.0f)	*h=hue2+360.0f;
			else
			{
				*h=hue2;
			}
		}
	}
	//else if(*h>150.0f && *h<330.0f)
	//{
	//	sss=*h-240.0f;
	//	if(sss<0.0f)	sss=-sss;
	//	sss/=100.0f;
	//	//satu=satu*(1-sss*(1.0f-(float)Rvalue));
	//	//hue=240.0f+(hue-240.0f)*(float)Rvalue;
	//}
}

void SettingTrackbar(int pos){
	modification_factor = (float)temp_factor / 100.f;
}

void inverse_SettingTrackbar(int pos){
	inverse_factor = (float)t_inverse_factor / 100.f;
}

void Refine_img(IplImage *src, IplImage *dst, float modification_factor, int MODE){
	float H, S, I;
	float refine_R, refine_G, refine_B;

	/*IplImage *HSV = cvCreateImage(cvGetSize(src), IPL_DEPTH_8U, 3);
	cvCvtColor(src, HSV, CV_BGR2HSV);
	for(int i = 0; i < HSV->width; i++){
		for(int j = 0; j < HSV->height; j++){
			float H = (unsigned char)HSV->imageData[i*3 + j*HSV->widthStep + 0];
			float S = (unsigned char)HSV->imageData[i*3 + j*HSV->widthStep + 1];
			float V = (unsigned char)HSV->imageData[i*3 + j*HSV->widthStep + 2];

			H = H/255.f*360.f;
			S = S/255.f;
			V = V/255.f;

			HSI_Revision(&H, &S, &V, modification_factor);

			H = H/360.f*255.f;
			S = S*255.f;
			V = V*255.f;

			HSV->imageData[i*3 + j*HSV->widthStep + 0] = (unsigned char)H;
			HSV->imageData[i*3 + j*HSV->widthStep + 1] = (unsigned char)S;
			HSV->imageData[i*3 + j*HSV->widthStep + 2] = (unsigned char)V;
		}
	}
	cvCvtColor(HSV, dst, CV_HSV2BGR);
	cvReleaseImage(&HSV);*/

	// 보정 알고리즘 수행
	for(register int i = 0; i < src->width; i++){
		for(register int j = 0; j < src->height; j++){
			unsigned char B = (unsigned char)src->imageData[i*3 + j*src->widthStep + 0];
			unsigned char G = (unsigned char)src->imageData[i*3 + j*src->widthStep + 1];
			unsigned char R = (unsigned char)src->imageData[i*3 + j*src->widthStep + 2];

			//RGB -> HSL
			RGB_To_HSI((float)R/255.f, (float)G/255.f, (float)B/255.f, &H, &S, &I);

			//색약 보정 수행
			if(MODE == MODE_CORRECTION)
				HSI_Revision(&H, &S, &I, modification_factor);
			else if(MODE == MODE_DYSCHROMATOPSA)
				Dyschromatopsa(&H, &S, &I, modification_factor);

			//HSL -> RGB
			HSI_To_RGB(H,S,I, &refine_R, &refine_G, &refine_B);
			refine_B *= 255.f, refine_G *= 255.f, refine_R *= 255.f;
			CLIP(refine_B, 0, 255); CLIP(refine_G, 0, 255); CLIP(refine_R, 0, 255);

			//refine_B = 100, refine_G = 100, refine_R = 100;

			dst->imageData[i*3 + j*dst->widthStep + 0] = (unsigned char)refine_B;
			dst->imageData[i*3 + j*dst->widthStep + 1] = (unsigned char)refine_G;
			dst->imageData[i*3 + j*dst->widthStep + 2] = (unsigned char)refine_R;
		}
	}
}

int main(){
	int mode;
	int camera_idx;

	char *ini_path = "./setting.ini";
	char file_path[256];

	IplImage *src_img = NULL, *dst_img = NULL;
	CvCapture *Capture = NULL;
	IplImage *Capture_img;

	while(1){
		printf("\n==========================================================================\n");
		printf("Select Menu\n");
		printf("1.Setting\n");
		printf("2.사진 보정\n");
		printf("3.동영상 보정\n");
		printf("4.Live Cam\n");
		printf("5.색각 이상 시뮬레이션\n");
		printf("6.색약 보정 & 색각 이상 체험\n");
		printf("7.exit\n");
		printf("==========================================================================\n\n");
		printf("menu : ");
		scanf("%d", &mode);

		switch(mode){
		case 1:
			{
			// 설정부
			cvNamedWindow("Color weakness test");
			cvCreateTrackbar("factor", "Color weakness test", &temp_factor, 100, SettingTrackbar);
			IplImage *test_img = cvLoadImage("test_img.jpg");
			IplImage *modify_img = cvCreateImage(cvGetSize(test_img), IPL_DEPTH_8U, 3);

			while(1){
				Refine_img(test_img, modify_img, modification_factor, MODE_CORRECTION);

				cvShowImage("Origin", test_img);
				cvShowImage("Color weakness test", modify_img);
				if(cvWaitKey(33) == 27)		break;
			}

			cvReleaseImage(&test_img);
			cvReleaseImage(&modify_img);

			cvDestroyAllWindows();
			}
			break;
		case 2:
			// 사진 보정
			printf("Image path : ");
			scanf("%s", file_path);
			src_img = cvLoadImage(file_path);
			if(src_img->width == 0){
				printf("ERROR : File not found\n");
				break;
			}else{
				dst_img = cvCreateImage(cvGetSize(src_img), IPL_DEPTH_8U, 3);
				Refine_img(src_img, dst_img, modification_factor, MODE_CORRECTION);

				cvShowImage("Source Image", src_img);
				cvShowImage("Destination Image", dst_img);
				cvWaitKey(0);

				cvReleaseImage(&src_img);
				cvReleaseImage(&dst_img);
				src_img = NULL;
				dst_img = NULL;

				cvDestroyAllWindows();
			}

			break;
		case 3:
			// 동영상 보정
			printf("Image path : ");
			scanf("%s", file_path);
			Capture = cvCaptureFromAVI(file_path);
			
			if(Capture == NULL){
				printf("ERROR : Video not found\n");
				break;
			}else{
				double nFPS = cvGetCaptureProperty(Capture, CV_CAP_PROP_FPS);
				//nFPS = 1000 / nFPS;
				int nTotalFrame = (int)cvGetCaptureProperty(Capture, CV_CAP_PROP_FRAME_COUNT);
				int frameCount = 1;

				Capture_img = cvQueryFrame(Capture);
				dst_img = cvCreateImage(cvGetSize(Capture_img), IPL_DEPTH_8U, 3);
				IplImage *small_src = cvCreateImage(cvSize(Capture_img->width/2, Capture_img->height/2), IPL_DEPTH_8U, 3);
				IplImage *small_dst = cvCloneImage(small_src);

				while(1){
					int _TICK = GetTickCount(); 

					Capture_img = cvQueryFrame(Capture);
					cvResize(Capture_img, small_src);

					Refine_img(small_src, small_dst, modification_factor, MODE_CORRECTION);
					cvResize(small_dst, dst_img);

					//cvShowImage("Source Image", src_img);
					
					frameCount++;

					if(cvWaitKey(1) == 27 || frameCount >= nTotalFrame-1){
						cvDestroyAllWindows();
						break;
					}

					_TICK = GetTickCount() - _TICK;
					float fps = 1000.0f / (float)_TICK;
					char buf[32];
					sprintf(buf, "%.2f fps", fps);
					cvPutText(dst_img, buf, cvPoint(30, 30), &cvFont(1.0), cvScalar(0,0,255));

					cvShowImage("Destination Image", dst_img);
				}

				cvReleaseImage(&small_src);
				cvReleaseImage(&small_dst);
				cvReleaseImage(&src_img);
				cvReleaseImage(&dst_img);

				Capture = NULL;

				src_img = NULL;
				dst_img = NULL;
			}
			break;

		case 4:
			// 카메라 보정
			printf("Select Camera index : ");
			scanf("%d", &camera_idx);
			Capture = cvCaptureFromCAM(camera_idx);
			if(Capture == NULL){
				printf("ERROR : Camera not found\n");
				break;
			}else{
				while(1){
					Capture_img = cvQueryFrame(Capture);
					src_img = cvCloneImage(Capture_img);
					dst_img = cvCreateImage(cvGetSize(src_img), IPL_DEPTH_8U, 3);

					Refine_img(src_img, dst_img, modification_factor, MODE_CORRECTION);

					cvShowImage("Source Image", src_img);
					cvShowImage("Destination Image", dst_img);

					cvReleaseImage(&src_img);
					cvReleaseImage(&dst_img);

					if(cvWaitKey(10) == 27){
						cvDestroyAllWindows();
						break;
					}
				}

				cvReleaseCapture(&Capture);
				Capture = NULL;

				src_img = NULL;
				dst_img = NULL;
			}

			break;
		case 5:
			// 색각 이상 체험
			printf("Image path : ");
			scanf("%s", file_path);
			src_img = cvLoadImage(file_path);
			if(src_img->width == 0){
				printf("ERROR : File not found\n");
				break;
			}else{
				// 설정부
				cvNamedWindow("Color weakness test");
				cvCreateTrackbar("factor", "Color weakness test", &temp_factor, 100, SettingTrackbar);
				IplImage *modify_img = cvCreateImage(cvGetSize(src_img), IPL_DEPTH_8U, 3);

				while(1){
					Refine_img(src_img, modify_img, modification_factor, MODE_DYSCHROMATOPSA);

					cvShowImage("Color weakness test", modify_img);
					if(cvWaitKey(33) == 27)		break;
				}

				cvReleaseImage(&src_img);
				cvReleaseImage(&modify_img);

				cvDestroyAllWindows();
			}
			break;
		case 6:
			{
			// 설정부
			cvNamedWindow("Color weakness test");
			cvNamedWindow("Inverse");
			cvCreateTrackbar("factor", "Color weakness test", &temp_factor, 100, SettingTrackbar);
			cvCreateTrackbar("factor", "Inverse", &t_inverse_factor, 100, inverse_SettingTrackbar);
			IplImage *test_img = cvLoadImage("test_img.jpg");
			IplImage *modify_img = cvCreateImage(cvGetSize(test_img), IPL_DEPTH_8U, 3);
			IplImage *inverseImg = cvCreateImage(cvGetSize(test_img), IPL_DEPTH_8U, 3);


			while(1){
				Refine_img(test_img, modify_img, modification_factor, MODE_CORRECTION);
				Refine_img(modify_img, inverseImg, inverse_factor, MODE_DYSCHROMATOPSA);

				cvShowImage("Origin", test_img);
				cvShowImage("Inverse", inverseImg);
				cvShowImage("Color weakness test", modify_img);
				if(cvWaitKey(33) == 27)		break;
			}

			cvReleaseImage(&inverseImg);
			cvReleaseImage(&test_img);
			cvReleaseImage(&modify_img);

			cvDestroyAllWindows();
			}
		case 7:
			return 0;
		default:
			printf("ERRER : select correct menu\n");
			break;
		}
	}

	return 0;
}
