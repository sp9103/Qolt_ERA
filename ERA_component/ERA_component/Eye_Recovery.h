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
	CColorConv *ConvColor;				//����ȯ Ŭ����
	
	UcharRGB ***Data_Matrix;
	int t_interval;

	/*���� ����*/
	void HSI_Revision(float *h, float *s, float *l, float modification_factor);
	/*���� ü��*/
	void Dyschromatopsa(float *h, float *s, float *l, float modification_factor);
	/*�ȼ� ���� ����*/
	void RefinePixel(uchar *R, uchar *G, uchar *B, float factor);
	/*�ȼ� ���� ü��*/
	void InversePixel(uchar *R, uchar *G, uchar *B, float factor);
	/*Find Pixel Match in Tree*/
	int MatchTreePixel(uchar *R, uchar *G, uchar *B, int interval);

public:
	Eye_Recovery(void);
	~Eye_Recovery(void);

	/*�̹��� ���� ���� - Static Image*/
	void RefineImage(cv::Mat src, cv::Mat dst, float factor);
	/*�̹��� ���� ü�� - Static Image*/
	void InverseImage(cv::Mat src, cv::Mat dst, float factor);
	/*Tree file Craete*/
	bool MakeTreeFile(int interval, float factor, char *FilePath, int mode);
	/*Using Tree, Make Image*/
	int MakeImage_to_Data(cv::Mat src, cv::Mat dst);
	/*Open Data File, �ǽð� ��ȯ ���Ժο��� Data ������ ���������
	- ������ ���� ���ۿ� ������ �� ������ ����*/
	int OpenDataFile(char *FilePath);
	/*Delete Data_Matrix. �ǽð� ��ȯ ���� �� ȣ��.*/
	void DeleteDataBuffer();
};

