#include "Eye_Recovery.h"


Eye_Recovery::Eye_Recovery(void)
{
	Data_Matrix = NULL;
}


Eye_Recovery::~Eye_Recovery(void)
{
}

void Eye_Recovery::HSI_Revision( float *h, float *s, float *l, float modification_factor )
{
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

void Eye_Recovery::Dyschromatopsa( float *h, float *s, float *l, float modification_factor )
{
	float hue2, sss;

	if((*h>=0.0f && *h<=150.0f) || (330.0f<=*h && *h<=360.0f))
	{
		if(*h>=0.0f && *h<=150.0f)	*h=60.0f+(*h-60.0f)*(float)modification_factor/1.4;
		else
		{
			hue2=60-(60.0f+(360.0f-*h))*(float)modification_factor/1.4;
			if(hue2<0.0f)	*h=hue2+360.0f;
			else
			{
				*h=hue2;
			}
		}
	}
	else if(*h>150.0f && *h<330.0f)
	{
		sss=*h-240.0f;
		if(sss<0.0f)	sss=-sss;
		sss/=100.0f;
	}
}

/*image color weakness correction - Static Image
	src : input Image
	dst : output Image
	factor : correction factor*/
void Eye_Recovery::RefineImage( cv::Mat src, cv::Mat dst, float factor )
{
	for(register int i = 0; i < src.rows; i++){
		for(register int j = 0; j < src.cols; j++){
			unsigned char B = src.at<cv::Vec3b>(i,j)[0];
			unsigned char G = src.at<cv::Vec3b>(i,j)[1];
			unsigned char R = src.at<cv::Vec3b>(i,j)[2];

			RefinePixel(&R, &G, &B, factor);

			//image insert
			dst.at<cv::Vec3b>(i,j)[0] = (unsigned char)B;
			dst.at<cv::Vec3b>(i,j)[1] = (unsigned char)G;
			dst.at<cv::Vec3b>(i,j)[2] = (unsigned char)R;
		}
	}
}

/*image color weakness correction - Static Image
	src : input Image
	dst : output Image
	factor : correction factor*/
void Eye_Recovery::InverseImage( cv::Mat src, cv::Mat dst, float factor )
{
	float H, S, I;
	float refine_R, refine_G, refine_B;

	for(register int i = 0; i < src.rows; i++){
		for(register int j = 0; j < src.cols; j++){
			unsigned char B = src.at<cv::Vec3b>(i,j)[0];
			unsigned char G = src.at<cv::Vec3b>(i,j)[1];
			unsigned char R = src.at<cv::Vec3b>(i,j)[2];
			
			InversePixel(&R, &G, &B, factor);

			//image insert
			dst.at<cv::Vec3b>(i,j)[0] = (unsigned char)B;
			dst.at<cv::Vec3b>(i,j)[1] = (unsigned char)G;
			dst.at<cv::Vec3b>(i,j)[2] = (unsigned char)R;
		}
	}
}

/*pixel color weakness correction
	R, G, B : unsigned char pixel value
	factor : correction factor*/
void Eye_Recovery::RefinePixel( uchar *R, uchar *G, uchar *B ,float factor )
{

	float H, S, I;
	float refine_R, refine_G, refine_B;

	//ConvertColor.RGB_To_HSI((float)*R/255.f, (float)*G/255.f, (float)*B/255.f, &H, &S, &I);
	HSI_Revision(&H, &S, &I, factor);
	//ConvertColor.HSI_To_RGB(H,S,I, &refine_R, &refine_G, &refine_B);

	refine_B *= 255.f, refine_G *= 255.f, refine_R *= 255.f;
	CLIP(refine_B, 0, 255); CLIP(refine_G, 0, 255); CLIP(refine_R, 0, 255);

	*R = (uchar)refine_R, *G = (uchar)refine_G, *B = (uchar)refine_B;
}

/*pixel color weakness experience
	R, G, B : unsigned char pixel value
	factor : correction factor*/
void Eye_Recovery::InversePixel( uchar *R, uchar *G, uchar *B, float factor )
{
	float H, S, I;
	float refine_R, refine_G, refine_B;

	//ConvertColor.RGB_To_HSI((float)*R/255.f, (float)*G/255.f, (float)*B/255.f, &H, &S, &I);
	Dyschromatopsa(&H, &S, &I, factor);
	//ConvertColor.HSI_To_RGB(H,S,I, &refine_R, &refine_G, &refine_B);

	refine_B *= 255.f, refine_G *= 255.f, refine_R *= 255.f;
	CLIP(refine_B, 0, 255); CLIP(refine_G, 0, 255); CLIP(refine_R, 0, 255);

	*R = (uchar)refine_R, *G = (uchar)refine_G, *B = (uchar)refine_B;
}

/*Tree file Create
	interval : makeing correction tree interval
	FilePath*/
bool Eye_Recovery::MakeTreeFile( int interval, float factor, const char *FilePath, int mode )
{
	FILE *TreeData = fopen(FilePath, "wb");
	uchar B,G,R;
	unsigned int t_B, t_G, t_R;

	int counts = 255/interval + 1;

	//read interval
	fwrite(&interval, sizeof(int), 1, TreeData);

	/*R, G, B order */
	for(register int i = 0; i < counts; i++){
		for(register int j = 0; j < counts; j++){
			for(register int k = 0; k < counts; k++){
				t_R = i * interval + interval/2;
				t_G = j * interval + interval/2;
				t_B = k * interval + interval/2;

				if(t_R > 255)	R = 255;
				else					R = t_R;
				if(t_G > 255)	G = 255;
				else					G = t_G;
				if(t_B > 255) B = 255;
				else					B = t_B;

				if(R > 255 || G > 255 || B > 255)	printf("convert pixel error\n");

				if(mode == MODE_CORRECTION)
					RefinePixel(&R, &G, &B, factor);
				else if(mode == MODE_DYSCHROMATOPSA)
					InversePixel(&R, &G, &B, factor);

				if(R > 255 || G > 255 || B > 255)	printf("refine pixel error\n");

				fwrite(&R, sizeof(char), 1, TreeData);
				fwrite(&G, sizeof(char), 1, TreeData);
				fwrite(&B, sizeof(char), 1, TreeData);
			}
		}
	}

	fclose(TreeData);

	return true;
}

/*Find Pixel Match in Tree
 R, G, B : target correction value
 interval : interval
 p_Data : saved data File pointer*/
int Eye_Recovery::MatchTreePixel( uchar *R, uchar *G, uchar *B, int interval )
{
	int idx_R, idx_G, idx_B;

	idx_R = *R /interval, idx_G = *G / interval, idx_B = *B / interval;
	//if(*R % t_interval != 0)	idx_R++;
	//if(*G % t_interval != 0)	idx_G++;
	//if(*B % t_interval != 0)	idx_B++;

	*R = Data_Matrix[idx_R][idx_G][idx_B].R;
	*G = Data_Matrix[idx_R][idx_G][idx_B].G;
	*B = Data_Matrix[idx_R][idx_G][idx_B].B;

	return 1;
}

/*Using Tree, Make Image
 src : input Image
 dst : output Image
 p_Data : saved data File pointer*/
int Eye_Recovery::MakeImage_to_Data( cv::Mat src, cv::Mat dst)
{
	for(register int i = 0; i < src.rows; i++){
		for(register int j = 0; j < src.cols; j++){
			unsigned char B = src.at<cv::Vec3b>(i,j)[0];
			unsigned char G = src.at<cv::Vec3b>(i,j)[1];
			unsigned char R = src.at<cv::Vec3b>(i,j)[2];

			MatchTreePixel(&R, &G, &B, t_interval);

			//image insert
			dst.at<cv::Vec3b>(i,j)[0] = (unsigned char)B;
			dst.at<cv::Vec3b>(i,j)[1] = (unsigned char)G;
			dst.at<cv::Vec3b>(i,j)[2] = (unsigned char)R;
		}
	}

	return 0;
}

int Eye_Recovery::OpenDataFile(const char *FilePath )
{
	FILE *p_Data = fopen(FilePath, "rb");
	if(p_Data == NULL)		return -1;

	fread(&t_interval, sizeof(int), 1, p_Data);

	int counts = 255/t_interval + 1;

	//Dynamic allocation
	Data_Matrix = (UcharRGB ***)malloc(sizeof(UcharRGB**) * counts);
	for(int i = 0; i < counts; i++){
		Data_Matrix[i] = (UcharRGB **)malloc(sizeof(UcharRGB*) * counts);
	}
	for(int i = 0; i < counts; i++){
		for(int j = 0; j < counts; j++){
			Data_Matrix[i][j] = (UcharRGB *)malloc(sizeof(UcharRGB) * counts);
		}
	}

	/*R, G, B order */
	for(register int i = 0; i < counts; i++){
		for(register int j = 0; j < counts; j++){
			for(register int k = 0; k < counts; k++){
				fread(&Data_Matrix[i][j][k].R, sizeof(uchar), 1, p_Data);
				fread(&Data_Matrix[i][j][k].G, sizeof(uchar), 1, p_Data);
				fread(&Data_Matrix[i][j][k].B, sizeof(uchar), 1, p_Data);
			}
		}
	}

	fclose(p_Data);

	return 1;
}

void Eye_Recovery::DeleteDataBuffer()
{
	free(Data_Matrix);
	Data_Matrix = NULL;
}