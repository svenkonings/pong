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
sem_t receiveProdMutex, sendProdMutex, sendConsMutex; // Thread safety mutexes, receiveCons is always a single thread
int receiveBuffer[N][3], sendBuffer[N];
int receiveIn = 0, receiveOut = 0, sendIn = 0, sendOut = 0;

// Called every edge of the clock
void trigger(void) {
	if (digitalRead(11)) { // Rising edge
		if (!output) { // Receiving
			// Convert pins to goal, mode and value
			int mode = 0, value = 0;
			int goal = digitalRead(pins[0]);
			for(int i = 1; i < 3; i++) {
				mode = mode * 2 + digitalRead(pins[i]);
			}
			for(int i = 3; i < 15; i++) {
				value = value * 2 + digitalRead(pins[i]);
			}
			// Add goal, mode and value to buffer
			sem_wait(&receiveSpaces);
			sem_wait(&receiveProdMutex);
			receiveBuffer[receiveIn][0] = mode;
			receiveBuffer[receiveIn][1] = value;
			receiveBuffer[receiveIn][2] = goal;
			receiveIn = (receiveIn + 1) % N;
			sem_post(&receiveProdMutex);
			sem_post(&receiveElements);
		} // Do nothing while sending
	} else { // Falling edge
		int sendData; // Sending data available boolean
		sem_wait(&sendConsMutex);
		sem_getvalue(&sendElements, &sendData); 
		if (sendData) { // Want to send
			if (!output) { // Previously not sending
				// Start sending
				output = 1;
				// Prepare pins
				digitalWrite(10, HIGH);
				for(int i = 0; i < 15; i++) {
					pinMode(pins[i], OUTPUT);
				}
			}
			// Read value from buffer
			sem_wait(&sendElements);
			int value = sendBuffer[sendOut];
			sendOut = (sendOut + 1) % N;
			sem_post(&sendSpaces);
			// Convert decimal to pins
			for (int i = 14; i >= 0; i--) {
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
			}
		}
		sem_post(&sendConsMutex);
	}
}

// Should be called once to start the program
JNIEXPORT void JNICALL Java_pong_gpio_Gpio_listen(JNIEnv *env, jobject thisObj) {
	// Get a reference to this object's class
	jclass thisClass = (*env)->GetObjectClass(env, thisObj);
	// Get the Method IDs for the listener methods
	jmethodID paddleLeft = (*env)->GetMethodID(env, thisClass, "paddleLeft", "(I)V");
	jmethodID paddleRight = (*env)->GetMethodID(env, thisClass, "paddleRight", "(I)V");
	jmethodID ballX = (*env)->GetMethodID(env, thisClass, "ballX", "(I)V");
	jmethodID ballY = (*env)->GetMethodID(env, thisClass, "ballY", "(I)V");
	jmethodID goalLeft = (*env)->GetMethodID(env, thisClass, "goalLeft", "()V");
	jmethodID goalRight = (*env)->GetMethodID(env, thisClass, "goalRight", "()V");
	jmethodID collision = (*env)->GetMethodID(env, thisClass, "collision", "()V");
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
		// Read goal, mode and value from buffer
		sem_wait(&receiveElements);
		int mode = receiveBuffer[receiveOut][0];
		int value = receiveBuffer[receiveOut][1];
		int goal = receiveBuffer[receiveOut][2];
		receiveOut = (receiveOut + 1) % N;
		sem_post(&receiveSpaces);
		// Call the methods
		switch(mode) {
			case 0:
				if (goal) {
					(*env)->CallVoidMethod(env, thisObj, goalLeft);
				}
				(*env)->CallVoidMethod(env, thisObj, paddleLeft, value);
				break;
			case 1:
				if (goal) {
					(*env)->CallVoidMethod(env, thisObj, goalRight);
				}
				(*env)->CallVoidMethod(env, thisObj, paddleRight, value);
				break;
			case 2:
				if (goal) {
					(*env)->CallVoidMethod(env, thisObj, collision);
				}
				(*env)->CallVoidMethod(env, thisObj, ballX, value);
				break;
			case 3:
				(*env)->CallVoidMethod(env, thisObj, ballY, value);
				break;
		}
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
	sendBuffer[sendIn] = (int) value;
	sendIn = (sendIn + 1) % N;
	sem_post(&sendProdMutex);
	sem_post(&sendElements);
}