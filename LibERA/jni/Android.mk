LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

OPENCV_CAMERA_MODULE:=on
OPENCV_INSTALL_MODULES:=on
include E:\Document\workspace\OpenCV-2.4.8-android-sdk\OpenCV-2.4.8-android-sdk\sdk\native\jni\OpenCV.mk

LOCAL_MODULE    := ERA
LOCAL_SRC_FILES := ERAcore.cpp
LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_LDLIBS += -llog -ldl

include $(BUILD_SHARED_LIBRARY)
