#include <stdio.h>
#include <windows.h>

#pragma  comment(lib, "winmm.lib")

#include "Eye_Recovery.h"

int main(){
	Eye_Recovery ERA;
	char buf[256];

	cv::Mat load_iamge = cv::imread("Tulips.jpg");
	cv::Mat load_image2 = cv::imread("Tulips.jpg");
	cv::imshow("Origin", load_iamge);

	DWORD time = timeGetTime();
	ERA.RefineImage(load_iamge, load_iamge, 0.4);
	time = timeGetTime() - time;
	printf("픽셀별 보정시간 : %dms\n", time);
	cv::imshow("TEST", load_iamge);

	printf("Make Tree...\n");
	time = timeGetTime();
	ERA.MakeTreeFile(10, 0.4, "DATA.bin", MODE_DYSCHROMATOPSA);
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


	//cv::Mat ressize_input/* = load_iamge.clone()*/;
	//cv::resize(load_iamge, load_iamge, cv::Size(0,0), 0.5, 0.5);

	//cv::imshow("resize test", load_iamge);

	//cv::waitKey(0);

	printf("Enter the Video path : ");
	scanf("%s", buf);
	cv::VideoCapture testAVI(buf);

	while(1){
		cv::Mat frame;
		testAVI >> frame;
		ERA.MakeImage_to_Data(frame, frame);
		imshow("AVI", frame);
		if(cv::waitKey(30) == 27)		break;
	}

	ERA.DeleteDataBuffer();

	load_image2.release();
	load_iamge.release();

	return 0;
}