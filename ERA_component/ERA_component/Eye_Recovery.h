#include "Definition.h"
#include "ColorConv.h"

typedef struct UcharRGB{
	uchar R;
	uchar G;
	uchar B;
}UcharRGB;

class Eye_Recovery
{
private:
	CColorConv *ConvColor;				//color convert clsee
	
	UcharRGB ***Data_Matrix;
	int t_interval;

	/*color weakness correction*/
	void HSI_Revision(float *h, float *s, float *l, float modification_factor);
	/*color weakness experience*/
	void Dyschromatopsa(float *h, float *s, float *l, float modification_factor);
	/*pixel color weakness correction*/
	void RefinePixel(uchar *R, uchar *G, uchar *B, float factor);
	/*pixel color weakness experience*/
	void InversePixel(uchar *R, uchar *G, uchar *B, float factor);
	/*Find Pixel Match in Tree*/
	int MatchTreePixel(uchar *R, uchar *G, uchar *B, int interval);

public:
	Eye_Recovery(void);
	~Eye_Recovery(void);

	/*image color weakness correction - Static Image*/
	void RefineImage(cv::Mat src, cv::Mat dst, float factor);
	/*image color weakness experience - Static Image*/
	void InverseImage(cv::Mat src, cv::Mat dst, float factor);
	/*Tree file Craete*/
	bool MakeTreeFile(int interval, float factor, char *FilePath, int mode);
	/*Using Tree, Make Image*/
	int MakeImage_to_Data(cv::Mat src, cv::Mat dst);
	/*Open Data File, call this func -> real time convert start (one time)
	  - file open -> copy buffer -> file close*/
	int OpenDataFile(char *FilePath);
	/*Delete Data_Matrix. call this func => real time convert exit*/
	void DeleteDataBuffer();
};

