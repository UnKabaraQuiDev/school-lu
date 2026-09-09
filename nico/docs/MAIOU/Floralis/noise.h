#ifndef NOISE_H
#define NOISE_H

#include <Arduino.h>

extern void initNOISE();
extern void measureNOISE();
extern void calculateAverageNOISE();
extern void calculateStatusNOISE();
extern void printOnLCDScreenNOISE();

#if DEBUG_MODE
extern void printToConsoleNOISE();
#endif

#endif
