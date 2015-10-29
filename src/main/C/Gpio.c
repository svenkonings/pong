#include <jni.h>
#include <stdio.h>
#include <wiringPi.h>
#include "pong_gpio_Gpio.h"

JNIEXPORT void JNICALL Java_pong_gpio_Gpio_listen(JNIEnv *env, jobject thisObj) {
	// Get a class reference for this object
	jclass thisClass = (*env)->GetObjectClass(env, thisObj);
	// Get the Method ID for method "receive"
	jmethodID midCallBack = (*env)->GetMethodID(env, thisClass, "receive", "(I)V");
	if (NULL == midCallBack) return;
	// Call back the method
	(*env)->CallVoidMethod(env, thisObj, midCallBack, 1);
	return;
}

JNIEXPORT void JNICALL Java_pong_gpio_Gpio_send(JNIEnv *env, jobject thisObj) {
	
}