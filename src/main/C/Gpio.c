#include <jni.h>
#include <stdio.h>
#include <unistd.h>
#include <wiringPi.h>
#include <math.h>
#include "pong_gpio_Gpio.h"

int fd[2];
int pins[15] = {15, 16, 8, 9, 7, 0, 1, 2, 3, 4, 5, 6, 12, 13, 14};
int valueSent = -1;

void trigger(void) {
	int pinsReceived[15] = {0};
	for(int i = 0; i < 15; i++) {
		pinsReceived[i] = digitalRead(pins[i]);
	}
	int valueReceived = 0;
	for(int i = 0; i < 15; i++) {
		if (pinsReceived[i]) {
			valueReceived += pow(2, i);
		}
	}
	write(fd[1], &valueReceived, sizeof(int));
}

JNIEXPORT void JNICALL Java_pong_gpio_Gpio_listen(JNIEnv *env, jobject thisObj) {
	jclass thisClass = (*env)->GetObjectClass(env, thisObj);
	jmethodID midCallBack = (*env)->GetMethodID(env, thisClass, "receive", "(I)V");
	if (NULL == midCallBack) return;
	pipe(fd);
	wiringPiSetup();
	piHiPri(99);
	pinMode(10, OUTPUT);
	for(int i = 0; i < 15; i++) {
		pinMode(pins[i], INPUT);
	}
	wiringPiISR(11, INT_EDGE_RISING, trigger);
	while(1) {
		int valueReceived;
		read(fd[0], &valueReceived, sizeof(int));
		(*env)->CallVoidMethod(env, thisObj, midCallBack, valueReceived);
	}
	close(fd[0]);
	close(fd[1]);
	return;
}

JNIEXPORT void JNICALL Java_pong_gpio_Gpio_send(JNIEnv *env, jobject thisObj, jint value) {
	valueSent = (int) value;
}