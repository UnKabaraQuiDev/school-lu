#include "humidity.h"
#include "globals.h"
#include "lcd.h"
#include "co2.h"

static unsigned int index = 0;
static const int SIZE = AVERAGE_PERIOD;
static float currentValue = 0;      // in %
static float recentValues[SIZE];
static float average = 0;
static const int rangeMin = 30;
static const int rangeMax = 60;

void initHUMID() {
  #if DEBUG_MODE
  Serial.print(F("[ HUMID ]  Initialising Humidity Sensor........"));
  #endif

  dht.begin();

  #if DEBUG_MODE
  Serial.println(F("  [ OK ]"));
  #endif
}

/*void measureHUMID() {
  float h = dht.readHumidity();
  if (!isnan(h)) {
    currentValue = h;
    recentValues[index++] = h;
    if (index >= SIZE) index = 0;
  }
  #if DEBUG_MODE
  if (isnan(currentValue)) {
    Serial.println(F("[ HUMID ]  Failed to read from DHT sensor!"));
  }
  #endif
}
/**/

void measureHUMID() {
  currentValue = getHUMID();
  recentValues[index++] = currentValue;
  if (index >= SIZE) index = 0;
}

/*
void updateHUMID(float h) {
  currentValue = h;
  recentValues[index++] = currentValue;
  if (index >= SIZE) index = 0;
}
/**/

void calculateAverageHUMID() {
  float sum = 0;
  int count = (index < SIZE) ? index : SIZE;
  for (int i = 0; i < count; i++) {
    sum += recentValues[i];
  }
  average = (count > 0) ? sum / count : 0;
  calculateStatusHUMID();
}

void calculateStatusHUMID() {
  if (average >= rangeMin && average <= rangeMax) {
    statusHUMID = "GOOD";
  } else if (average < rangeMin) {
    statusHUMID = "POOR";
  } else if (average > rangeMax) {
    statusHUMID = "HIGH";
  }
}

/*void printOnLCDScreenHUMID() {
  printMultipleLineOnLCD("Humidity", String(currentValue) + " %");
  lcd.setCursor(11, 0);
  lcd.print("|" + statusHUMID);
  lcd.setCursor(11, 1);
  lcd.print("|");
}/**/

void printOnLCDScreenHUMID() {

  clearScreenLCD();

  // line 1
  OpenLCD.write(0xFE);
  OpenLCD.write(0x80);
  OpenLCD.print("Humidity");

  // line 2
  OpenLCD.write(0xFE);
  OpenLCD.write(0xC0);
  OpenLCD.print(String(round(currentValue)) + " %");

  // column 12 indicator
  OpenLCD.write(0xFE);
  OpenLCD.write(0x8B);   // row 1 col 12
  OpenLCD.write(255);    // full block
  OpenLCD.print(statusHUMID);

  OpenLCD.write(0xFE);
  OpenLCD.write(0xCB);   // row 2 col 12
  OpenLCD.write(255);    // full block
}

#if DEBUG_MODE
void printToConsoleHUMID() {
  Serial.print(F("[ HUMID ]  Current Humidity: "));
  Serial.print(currentValue);
  Serial.println(F(" %"));
  Serial.print(F("[ HUMID ]  Average Humidity: "));
  Serial.print(average);
  Serial.println(F(" %"));
  Serial.print(F("[ HUMID ]  Range: "));
  Serial.print(rangeMin);
  Serial.print(" - ");
  Serial.print(rangeMax);
  Serial.print(F("                |  Status: "));
  Serial.println(statusHUMID);
}
#endif
