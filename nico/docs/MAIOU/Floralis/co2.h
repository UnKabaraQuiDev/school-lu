#ifndef CO2LV_H
#define CO2LV_H

#include <Arduino.h>
#include <SensirionI2cScd4x.h>
#include <Wire.h>

extern void initCO2LV();
extern void measureCO2LV();
extern float getTEMPR();
extern float getHUMID();
extern void calculateAverageCO2LV();
extern void calculateStatusCO2LV();
extern void printOnLCDScreenCO2LV();

#if DEBUG_MODE
extern void printToConsoleCO2LV();
#endif

#endif
