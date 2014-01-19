#include <stdio.h>
#include <windows.h>

#pragma  comment(lib, "winmm.lib")

#include "Eye_Recovery.h"

int main(){
	Eye_Recovery ERA;

	cv::Mat load_iamge = cv::imread("Tulips.jpg");
	cv::Mat load_image2 = cv::imread("Tulips.jpg");
	cv::Mat no_image = cv::imread("no_image.jpg");
	cv::imshow("Origin2", no_image);
	cv::imshow("Origin", load_iamge);

	DWORD time = timeGetTime();
	ERA.RefineImage(load_iamge, load_iamge, 0.4);
	time = timeGetTime() - time;
	printf("픽셀별 보정시간 : %dms\n", time);
	ERA.InverseImage(no_image, no_image, 0.0);

	cv::imshow("Inverse Image", no_image);
	cv::imshow("TEST", load_iamge);

	printf("Make Tree...\n");
	time = timeGetTime();
	ERA.MakeTreeFile(2, 0.4, "DATA.bin", MODE_CORRECTION);
	time = timeGetTime() - time;
	printf("Make Tree time : %dms\n", time);
	time = timeGetTime();
	ERA.OpenDataFile("DATA.bin");
	time = timeGetTime() - time;
	printf("Open & Create Tree time : %dms\n", time);
	printf("Make Tree complete!\n");

	time = timeGetTime();
	ERA.MakeImage_to_Data(load_image2, load_iamge);
	time = timeGetTime() - time;
	printf("Make Image to Data time : %dms\n", time);
	cv::imshow("Real Time Test", load_iamge);
	cv::waitKey(0);

	ERA.DeleteDataBuffer();

	load_image2.release();
	load_iamge.release();

	return 0;
}