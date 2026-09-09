#include "globals.h"

// --- Floralis.ino ---

unsigned long lastCO2LV = 0;
unsigned long lastHUMID = 0;
unsigned long lastLIGHT = 0;
unsigned long lastNOISE = 0;
unsigned long lastTEMPR = 0;

unsigned long lastAVERAGE = 0;
unsigned long lastTOGGLE = 0;
unsigned long lastCONSOLE = 0;

bool toggleHUMIDorTEMPR = false;

String statusCO2LV = "";    // "GOOD" or "POOR" or "HIGH"
String statusHUMID = "";    //
String statusLIGHT = "";
String statusNOISE = "";
String statusTEMPR = "";

unsigned int rgbLEDstatus;
/*
  0 (green)  --> all ok
  1 (orange) --> min 3 ok
  2 (red)    --> only 1 ok
*/

// --- LCD Display ---

/*const int rsPIN = 7, enPIN = 8, d4PIN = 9, d5PIN = 10, d6PIN = 11, d7PIN = 12;
LiquidCrystal lcd(rsPIN, enPIN, d4PIN, d5PIN, d6PIN, d7PIN);*/
// const int lcdBbacklightPIN = 13;

//SoftwareSerial OpenLCD(0, 1);   // RX, TX (not used)
//SoftwareSerial OpenLCD(2, 3);   // RX, TX
SoftwareSerial OpenLCD(0, 2);   // RX, TX

// --- CO2 Sensor ---

SensirionI2cScd4x sensor;
char errorMessage[64];
int16_t error;

// --- Noise Sensor ---

bool soundIsOn = false;
int soundThreshold = 500;

// --- Temperature & Humidity Sensor ---

DHT dht(temperatureSensorDO_PIN, DHTTYPE);
