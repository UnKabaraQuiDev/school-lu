#ifndef TEMPR_H
#define TEMPR_H

#include <Arduino.h>
#include <DHT.h>

extern void initTEMPR();
extern void measureTEMPR();
//extern void updateTEMPR(float t);
extern void calculateAverageTEMPR();
extern void calculateStatusTEMPR();
extern void printOnLCDScreenTEMPR();

#if DEBUG_MODE
extern void printToConsoleTEMPR();
#endif

#endif
