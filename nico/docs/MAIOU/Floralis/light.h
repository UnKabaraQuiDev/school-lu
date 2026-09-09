#ifndef LIGHT_H
#define LIGHT_H

#include <Arduino.h>

extern void initLIGHT();
extern void measureLIGHT();
extern void calculateAverageLIGHT();
extern void calculateStatusLIGHT();
extern void printOnLCDScreenLIGHT();

#if DEBUG_MODE
extern void printToConsoleLIGHT();
#endif

#endif
