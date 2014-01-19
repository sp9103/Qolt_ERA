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
	CColorConv *ConvColor;				//색변환 클래스
	
	UcharRGB ***Data_Matrix;
	int t_interval;

	/*색약 보정*/
	void HSI_Revision(float *h, float *s, float *l, float modification_factor);
	/*색약 체험*/
	void Dyschromatopsa(float *h, float *s, float *l, float modification_factor);
	/*픽셀 색약 보정*/
	void RefinePixel(uchar *R, uchar *G, uchar *B, float factor);
	/*픽셀 색약 체험*/
	void InversePixel(uchar *R, uchar *G, uchar *B, float factor);
	/*Find Pixel Match in Tree*/
	int MatchTreePixel(uchar *R, uchar *G, uchar *B, int interval);

public:
	Eye_Recovery(void);
	~Eye_Recovery(void);

	/*이미지 색약 보정 - Static Image*/
	void RefineImage(cv::Mat src, cv::Mat dst, float factor);
	/*이미지 색약 체험 - Static Image*/
	void InverseImage(cv::Mat src, cv::Mat dst, float factor);
	/*Tree file Craete*/
	bool MakeTreeFile(int interval, float factor, char *FilePath, int mode);
	/*Using Tree, Make Image*/
	int MakeImage_to_Data(cv::Mat src, cv::Mat dst);
	/*Open Data File, 실시간 변환 도입부에서 Data 파일을 열어줘야함
	- 파일을 열고 버퍼에 복사한 뒤 파일을 닫음*/
	int OpenDataFile(char *FilePath);
	/*Delete Data_Matrix. 실시간 변환 종료 시 호출.*/
	void DeleteDataBuffer();
};

