LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := ERA
LOCAL_SRC_FILES := ERAcore.cpp
LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_LDLIBS += -llog -ldl

include $(BUILD_SHARED_LIBRARY)
