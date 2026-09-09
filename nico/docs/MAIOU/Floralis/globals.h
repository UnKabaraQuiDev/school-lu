#ifndef GLOBALS_H
#define GLOBALS_H

#include <Arduino.h>
//#include <LiquidCrystal.h>      // for lcd.h
#include <SoftwareSerial.h>     // for lcd.h
#include <SensirionI2cScd4x.h>  // for co2.h
#include <Wire.h>               // for co2.h
#include <DHT.h>                // for temperature.h

// --- Floralis.ino ---

#define DEBUG_MODE 1      // 1 --> include ; 0 --> skip

#define INTERVAL_MEASURE_CO2LV      10000
#define INTERVAL_MEASURE_HUMID      5000
#define INTERVAL_MEASURE_LIGHT      5000
#define INTERVAL_MEASURE_NOISE      10
#define INTERVAL_MEASURE_TEMPR      5000

#define INTERVAL_CALCULATE_AVERAGE  60000
#define INTERVAL_TOGGLE_LCD_SCREEN  6000
#define INTERVAL_PRINT_TO_CONSOLE   5000

extern unsigned long lastCO2LV;
extern unsigned long lastHUMID;
extern unsigned long lastLIGHT;
extern unsigned long lastNOISE;
extern unsigned long lastTEMPR;

extern unsigned long lastAVERAGE;
extern unsigned long lastTOGGLE;
extern unsigned long lastCONSOLE;

#define AVERAGE_PERIOD 10;

extern bool toggleHUMIDorTEMPR;

extern String statusCO2LV;
extern String statusHUMID;
extern String statusLIGHT;
extern String statusNOISE;
extern String statusTEMPR;

extern unsigned int rgbLEDstatus;

// --- LCD Display ---

/*#define lcdBacklight_PIN 13         // Digital Pin
extern const int rsPIN, enPIN, d4PIN, d5PIN, d6PIN, d7PIN;
extern LiquidCrystal lcd;*/
//extern const int backlightPIN;

extern SoftwareSerial OpenLCD;

// --- CO2 Sensor ---

#define SCD4X_I2C_ADDR SCD41_I2C_ADDR_62  // or SCD40

#ifdef NO_ERROR
#undef NO_ERROR
#endif
#define NO_ERROR 0

extern SensirionI2cScd4x sensor;
extern char errorMessage[64];
extern int16_t error;

// --- Light Sensor ---

#define photoResistorAO_PIN 15      // Analog Pin (A1)

// --- Noise Sensor ---

#define soundSensorDO_PIN 3         // Digital Pin
#define soundSensorAO_PIN A0        // Analog Pin A0 / 14
#define soundLED_PIN 2              // Digital Pin
extern bool soundIsOn;
extern int soundThreshold;

// --- Temperature & Humidity Sensor ---

#define temperatureSensorDO_PIN 5   // Digital Pin
//#define DHTTYPE DHT22
#define DHTTYPE DHT11
extern DHT dht;

#endif
