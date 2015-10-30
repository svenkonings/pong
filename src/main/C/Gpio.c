#include <jni.h>
#include <stdio.h>
#include <semaphore.h>
#include <wiringPi.h>
#include "pong_gpio_Gpio.h"

#define N 10 // Buffer size

int pins[15] = {14, 13, 12, 6, 5, 4, 3, 2, 1, 0, 7, 9, 8, 16, 15}; // Pin mapping, first value is MSB
int output = 0; // Sending or receiving boolean

// Uses Producer-Consumer model for data transfer between threads
sem_t receiveElements, receiveSpaces, sendElements, sendSpaces;
sem_t receiveProdMutex, sendProdMutex, sendConsMutex; // Thread safety, receiveCons is always a single thread
int receiveBuffer[N], sendBuffer[N];
int receiveIn = 0, receiveOut = 0, sendIn = 0, sendOut = 0;

// Called every edge of the clock
void trigger(void) {
//	printf("Clock: %d, Output: %d\n", digitalRead(11), output);
	if (digitalRead(11)) { // Rising edge
		if (!output) { // Receiving
			// Convert pins to decimal value
			int value = 0;
			for(int i = 0; i < 15; i++) {
//				printf("ReadPin: %d\n", digitalRead(pins[i]));
				value = value * 2 + digitalRead(pins[i]);
			}
			// Add value to buffer
			sem_wait(&receiveSpaces);
			sem_wait(&receiveProdMutex);
//			printf("ReceiveWrite: %d\n", value);
			receiveBuffer[receiveIn] = value;
			receiveIn = (receiveIn + 1) % N;
			sem_post(&receiveProdMutex);
			sem_post(&receiveElements);
		} // Do nothing while sending
	} else { // Falling edge
		int sendData; // Sending data available boolean
		sem_wait(&sendConsMutex);
		sem_getvalue(&sendElements, &sendData); 
//		printf("sendData: %d\n", sendData);
		if (sendData) { // Want to send
			if (!output) { // Previously not sending
				// Start sending
				output = 1;
				// Prepare pins
				digitalWrite(10, HIGH);
				for(int i = 0; i < 15; i++) {
					pinMode(pins[i], OUTPUT);
				}
//				printf("Output true\n");
			}
			// Read value from buffer
			sem_wait(&sendElements);
//			printf("sendRead: %d\n", sendBuffer[sendOut]);
			int value = sendBuffer[sendOut];
			sendOut = (sendOut + 1) % N;
			sem_post(&sendSpaces);
			// Convert decimal to pins
			for (int i = 0; i < 15; i++) {
//				printf("WritePin: %d\n", (value >> i) & 1);
				digitalWrite(pins[i], (value >> i) & 1);
			}
		} else { // Don't want to send
			if (output) { // Previously sending
				// Stop sending
				output = 0;
				// Prepare pins
				digitalWrite(10, LOW);
				for(int i = 0; i < 15; i++) {
					pinMode(pins[i], INPUT);
				}
//				printf("Output false\n");
			}
		}
		sem_post(&sendConsMutex);
	}
}

// Should be called once to start the program
JNIEXPORT void JNICALL Java_pong_gpio_Gpio_listen(JNIEnv *env, jobject thisObj) {
	// Get a reference to this object's class
	jclass thisClass = (*env)->GetObjectClass(env, thisObj);
	// Get the Method ID for method "receive"
	jmethodID midCallBack = (*env)->GetMethodID(env, thisClass, "receive", "(I)V");
	// Initialize semaphores
	sem_init(&receiveElements, 0, 0);
    sem_init(&receiveSpaces, 0, N);
	sem_init(&sendElements, 0, 0);
    sem_init(&sendSpaces, 0, N);
	sem_init(&receiveProdMutex, 0, 1);
	sem_init(&sendProdMutex, 0, 1);
	sem_init(&sendConsMutex, 0, 1);
	// Initialize wiringPi
	wiringPiSetup();
	piHiPri(99);
	// Initialize pins
	pinMode(10, OUTPUT);
	digitalWrite(10, LOW);
	for(int i = 0; i < 15; i++) {
		pinMode(pins[i], INPUT);
	}
	wiringPiISR(11, INT_EDGE_BOTH, trigger);
	// Receive loop
	while(1) {
		// Read value from buffer
		sem_wait(&receiveElements);
//		printf("ReceiveRead: %d\n", receiveBuffer[receiveOut]);
		(*env)->CallVoidMethod(env, thisObj, midCallBack, receiveBuffer[receiveOut]);
		receiveOut = (receiveOut + 1) % N;
		sem_post(&receiveSpaces);
	}
	// Destroy semaphores
	sem_destroy(&receiveElements);
    sem_destroy(&receiveSpaces);
	sem_destroy(&sendElements);
    sem_destroy(&sendSpaces);
	sem_destroy(&receiveProdMutex);
	sem_destroy(&sendProdMutex);
	sem_destroy(&sendConsMutex);
	return;
}

// Should only be called while the program is listening
JNIEXPORT void JNICALL Java_pong_gpio_Gpio_send(JNIEnv *env, jobject thisObj, jint value) {
	// Add value to buffer
	sem_wait(&sendSpaces);
	sem_wait(&sendProdMutex);
//	printf("SendWrite: %d\n", value);
	sendBuffer[sendIn] = (int) value;
	sendIn = (sendIn + 1) % N;
	sem_post(&sendProdMutex);
	sem_post(&sendElements);
}