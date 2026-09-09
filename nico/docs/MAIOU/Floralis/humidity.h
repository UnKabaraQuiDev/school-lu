#ifndef HUMID_H
#define HUMID_H

#include <Arduino.h>
#include <DHT.h>

extern void initHUMID();
extern void measureHUMID();
//extern void updateHUMID(float h);
extern void calculateAverageHUMID();
extern void calculateStatusHUMID();
extern void printOnLCDScreenHUMID();

#if DEBUG_MODE
extern void printToConsoleHUMID();
#endif

#endif
