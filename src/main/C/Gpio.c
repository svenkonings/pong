#include <jni.h>
#include <stdio.h>
#include <semaphore.h>
#include <wiringPi.h>
#include "pong_gpio_Gpio.h"

#define PINS 15 // Number of pins
#define CLOCK 11 // Clock pin
#define DIRECTION 10 // Direction pin
#define MODE 3 // Pins for mode
#define BUFFER 10 // Buffer size

int pins[PINS] = {14, 13, 12, 6, 5, 4, 3, 2, 1, 0, 7, 9, 8, 16, 15}; // Pin mapping, first value is MSB
int output = 0; // Sending or receiving boolean

// Uses Producer-Consumer model for data transfer between threads
sem_t receiveElements, receiveSpaces, sendElements, sendSpaces;
sem_t receiveProdMutex, sendProdMutex, sendConsMutex; // Thread safety mutexes, receiveCons is always a single thread
int receiveBuffer[BUFFER][3], sendBuffer[BUFFER];
int receiveIn = 0, receiveOut = 0, sendIn = 0, sendOut = 0;

// Called every edge of the clock
void trigger(void) {
	if (digitalRead(CLOCK)) { // Rising edge
		if (!output) { // Receiving
			// Convert pins to mode, goal and value
			int mode = 0;
			for(int i = 0; i < MODE; i++) {
				mode = mode * 2 + digitalRead(pins[i]);
			}
			int goal = digitalRead(pins[MODE]);
			int value = 0;
			for(int i = MODE + 1; i < PINS; i++) {
				value = value * 2 + digitalRead(pins[i]);
			}
			// Write mode, goal and value to buffer
			printf("RECEIVED: mode = %d, goal = %d, value = %d\n", mode, goal, value);
			if (0 == mode) return; // Received nothing
			sem_wait(&receiveSpaces);
			sem_wait(&receiveProdMutex);
			receiveBuffer[receiveIn][0] = mode;
			receiveBuffer[receiveIn][1] = goal;
			receiveBuffer[receiveIn][2] = value;
			receiveIn = (receiveIn + 1) % BUFFER;
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
				digitalWrite(DIRECTION, HIGH);
				for(int i = 0; i < PINS; i++) {
					pinMode(pins[i], OUTPUT);
				}
			}
			// Read value from buffer
			sem_wait(&sendElements);
			int value = sendBuffer[sendOut];
			sendOut = (sendOut + 1) % BUFFER;
			sem_post(&sendSpaces);
			// Convert decimal to pins
			for (int i = 0; i < PINS; i++) {
				digitalWrite(pins[PINS - 1 - i], (value >> i) & 1);
			}
		} else { // Don't want to send
			if (output) { // Previously sending
				// Stop sending
				output = 0;
				// Prepare pins
				digitalWrite(DIRECTION, LOW);
				for(int i = 0; i < PINS; i++) {
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
	jmethodID goalLeft = (*env)->GetMethodID(env, thisClass, "goalLeft", "()V");
	jmethodID paddleRight = (*env)->GetMethodID(env, thisClass, "paddleRight", "(I)V");
	jmethodID goalRight = (*env)->GetMethodID(env, thisClass, "goalRight", "()V");
	jmethodID ballX = (*env)->GetMethodID(env, thisClass, "ballX", "(I)V");
	jmethodID collision = (*env)->GetMethodID(env, thisClass, "collision", "()V");
	jmethodID ballY = (*env)->GetMethodID(env, thisClass, "ballY", "(I)V");
	jmethodID calibration = (*env)->GetMethodID(env, thisClass, "calibration", "(I)V");
	// Initialize semaphores
	sem_init(&receiveElements, 0, 0);
	sem_init(&receiveSpaces, 0, BUFFER);
	sem_init(&sendElements, 0, 0);
	sem_init(&sendSpaces, 0, BUFFER);
	sem_init(&receiveProdMutex, 0, 1);
	sem_init(&sendProdMutex, 0, 1);
	sem_init(&sendConsMutex, 0, 1);
	// Initialize wiringPi
	wiringPiSetup();
	piHiPri(99);
	// Initialize pins
	pinMode(DIRECTION, OUTPUT);
	digitalWrite(DIRECTION, LOW);
	for(int i = 0; i < PINS; i++) {
		pinMode(pins[i], INPUT);
	}
	wiringPiISR(CLOCK, INT_EDGE_BOTH, trigger);
	// Receive loop
	while(1) {
		// Read mode, goal and value from buffer
		sem_wait(&receiveElements);
		int mode = receiveBuffer[receiveOut][0];
		int goal = receiveBuffer[receiveOut][1];
		int value = receiveBuffer[receiveOut][2];
		receiveOut = (receiveOut + 1) % BUFFER;
		sem_post(&receiveSpaces);
		// Call the methods
		switch(mode) {
			case 1:
				if (goal) {
					(*env)->CallVoidMethod(env, thisObj, goalLeft);
				}
				(*env)->CallVoidMethod(env, thisObj, paddleLeft, value);
				break;
			case 2:
				if (goal) {
					(*env)->CallVoidMethod(env, thisObj, goalRight);
				}
				(*env)->CallVoidMethod(env, thisObj, paddleRight, value);
				break;
			case 3:
				if (goal) {
					(*env)->CallVoidMethod(env, thisObj, collision);
				}
				(*env)->CallVoidMethod(env, thisObj, ballX, value);
				break;
			case 4:
				(*env)->CallVoidMethod(env, thisObj, ballY, value);
				break;
			case 5:
				(*env)->CallVoidMethod(env, thisObj, calibration, value);
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
	// Write value to buffer
	printf("SEND: value = %d\n", value);
	sem_wait(&sendSpaces);
	sem_wait(&sendProdMutex);
	sendBuffer[sendIn] = (int) value;
	sendIn = (sendIn + 1) % BUFFER;
	sem_post(&sendProdMutex);
	sem_post(&sendElements);
}