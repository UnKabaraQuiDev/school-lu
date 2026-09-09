#ifndef LCD_H
#define LCD_H

#include <Arduino.h>
//#include <LiquidCrystal.h>
#include <SoftwareSerial.h>

extern void initLCD();
extern void setContrast(int contrastValue);
extern void printMultipleLineOnLCD(String line1, String line2);
extern void clearScreenLCD();
//extern void dimBackgroundLightLCD(int percent);
extern void changeColorLCD(String color);

#endif
