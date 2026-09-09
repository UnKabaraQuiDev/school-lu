#include "temperature.h"
#include "globals.h"
#include "lcd.h"
#include "co2.h"

static unsigned int index = 0;
static const int SIZE = AVERAGE_PERIOD;
static float currentValue = 0;      // in °C
static float recentValues[SIZE];
static float average = 0;
static const int rangeMin = 19;
static const int rangeMax = 23;

void initTEMPR() {
  #if DEBUG_MODE
  Serial.print(F("[ TEMPR ]  Initialising Temperature Sensor....."));
  #endif

  //pinMode(temperatureSensorDO_PIN, INPUT);
  //dht.begin();
  // --> already in humidity.cpp

  #if DEBUG_MODE
  Serial.println(F("  [ OK ]"));
  #endif
}

/*void measureTEMPR() {
  float t = dht.readTemperature();
  if (!isnan(t)) {
    currentValue = t;
    currentValue = getTEMPR();  // <----------
    recentValues[index++] = t;
    if (index >= SIZE) index = 0;
  }
  #if DEBUG_MODE
  if (isnan(currentValue)) {
    Serial.println(F("[ TEMPR ]  Failed to read from DHT sensor!"));
  }
  #endif
}
/**/

void measureTEMPR() {
  currentValue = getTEMPR();
  recentValues[index++] = currentValue;
  if (index >= SIZE) index = 0;
}

/*
void updateTEMPR(float t) {
  currentValue = t;
  recentValues[index++] = currentValue;
  if (index >= SIZE) index = 0;
}
/**/

void calculateAverageTEMPR() {
  float sum = 0;
  int count = (index < SIZE) ? index : SIZE;
  for (int i = 0; i < count; i++) {
    sum += recentValues[i];
  }
  average = (count > 0) ? sum / count : 0;
  calculateStatusTEMPR();
}

void calculateStatusTEMPR() {
  if (average >= rangeMin && average <= rangeMax) {
    statusTEMPR = "GOOD";
  } else if (average < rangeMin) {
    statusTEMPR = "POOR";
  } else if (average > rangeMax) {
    statusTEMPR = "HIGH";
  }
}

/*void printOnLCDScreenTEMPR() {
  printMultipleLineOnLCD("Temperature", String(currentValue) + " " + (char)223 + "C");
  lcd.setCursor(11, 0);
  lcd.print("|" + statusTEMPR);
  lcd.setCursor(11, 1);
  lcd.print("|");
}/**/

void printOnLCDScreenTEMPR() {

  clearScreenLCD();

  // line 1
  OpenLCD.write(0xFE);
  OpenLCD.write(0x80);
  OpenLCD.print("Temperature");

  // line 2
  OpenLCD.write(0xFE);
  OpenLCD.write(0xC0);
  OpenLCD.print(String(round(currentValue)) + (char)223 + "C");

  // column 12 indicator
  OpenLCD.write(0xFE);
  OpenLCD.write(0x8B);   // row 1 col 12
  OpenLCD.write(255);    // full block
  OpenLCD.print(statusTEMPR);

  OpenLCD.write(0xFE);
  OpenLCD.write(0xCB);   // row 2 col 12
  OpenLCD.write(255);    // full block
}

#if DEBUG_MODE
void printToConsoleTEMPR() {
  Serial.print(F("[ TEMPR ]  Current Temperature: "));
  Serial.print(currentValue);
  Serial.println(F(" °C"));
  Serial.print(F("[ TEMPR ]  Average Temperature: "));
  Serial.print(average);
  Serial.println(F(" °C"));
  Serial.print(F("[ TEMPR ]  Range: "));
  Serial.print(rangeMin);
  Serial.print(" - ");
  Serial.print(rangeMax);
  Serial.print(F("                |  Status: "));
  Serial.println(statusTEMPR);
}
#endif

/*
void measureThermistor() {
  float temperature = thermistor.read();    // Temperature in Celsius
  Serial.print(F("Temperature: "));
  Serial.print(temperature);
  Serial.println(F(" °C"));
}
/**/