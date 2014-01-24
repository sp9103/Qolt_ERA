LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

OPENCV_CAMERA_MODULE:=on
OPENCV_INSTALL_MODULES:=on
include /Users/naubull2/Documents/Capstone/OpenCV-2.4.4-android-sdk/sdk/native/jni/OpenCV.mk

LOCAL_MODULE    := ERA
LOCAL_SRC_FILES := ERAcore.cpp
LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_LDLIBS += -llog -ldl

include $(BUILD_SHARED_LIBRARY)
