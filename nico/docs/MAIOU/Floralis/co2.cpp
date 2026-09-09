#include "co2.h"
#include "globals.h"
#include "lcd.h"

static unsigned int index = 0;
static const int SIZE = AVERAGE_PERIOD;
static float currentValue = 0;      // in ppm
static float recentValues[SIZE];
static float average = 0;
static const int rangeMin = 0;
static const int rangeMax = 2000;

static float currentTEMPR;
static float currentHUMID;

void initCO2LV() {
  #if DEBUG_MODE
  Serial.print(F("[ CO2LV ]  Initialising CO2 Sensor............."));
  #endif

  Wire.begin();
  sensor.begin(Wire, SCD41_I2C_ADDR_62);

  error = sensor.wakeUp();
  sensor.stopPeriodicMeasurement();
  sensor.reinit();
  error = sensor.startPeriodicMeasurement();
  
  #if DEBUG_MODE
  Serial.println(F("  [ OK ]"));
  #endif
}

void measureCO2LV() {
  bool dataReady = false;
  uint16_t co2 = 0;
  float t = 0, rh = 0;

  error = sensor.getDataReadyStatus(dataReady);
  if (error != NO_ERROR || !dataReady) return;

  error = sensor.readMeasurement(co2, t, rh);
  if (error == NO_ERROR) {
    //Serial.println(String(co2) + " " + String(t) + " " + String(rh));
    currentValue = co2;
    currentTEMPR = t;
    currentHUMID = rh;

    recentValues[index++] = currentValue;
    if (index >= SIZE) index = 0;
  }
}

float getTEMPR() {
  return currentTEMPR;
}

float getHUMID() {
  return currentHUMID;
}

void calculateAverageCO2LV() {
  float sum = 0;
  int count = (index < SIZE) ? index : SIZE;
  for (int i = 0; i < count; i++) {
    sum += recentValues[i];
  }
  average = (count > 0) ? sum / count : 0;
  calculateStatusCO2LV();
}

void calculateStatusCO2LV() {
  if (average >= rangeMin && average <= rangeMax) {
    statusCO2LV = "GOOD";
  } else if (average < rangeMin) {
    statusCO2LV = "POOR";
  } else if (average > rangeMax) {
    statusCO2LV = "HIGH";
  }
}

/*void printOnLCDScreenCO2LV() {
  printMultipleLineOnLCD("CO2 level", String(round(currentValue)) + " ppm");
  lcd.setCursor(11, 0);
  lcd.print("|" + statusCO2LV);
  lcd.setCursor(11, 1);
  lcd.print("|");
}/**/

void printOnLCDScreenCO2LV() {

  clearScreenLCD();

  // line 1
  OpenLCD.write(0xFE);
  OpenLCD.write(0x80);
  OpenLCD.print("CO2 level");

  // line 2
  OpenLCD.write(0xFE);
  OpenLCD.write(0xC0);
  OpenLCD.print(String(round(currentValue)) + " ppm");

  // column 12 indicator
  OpenLCD.write(0xFE);
  OpenLCD.write(0x8B);   // row 1 col 12
  OpenLCD.write(255);    // full block
  OpenLCD.print(statusCO2LV);

  OpenLCD.write(0xFE);
  OpenLCD.write(0xCB);   // row 2 col 12
  OpenLCD.write(255);    // full block
}

#if DEBUG_MODE
void printToConsoleCO2LV() {
  Serial.print(F("[ CO2LV ]  Current CO2 Level: "));
  Serial.print(currentValue);
  Serial.println(F(" ppm"));
  Serial.print(F("[ CO2LV ]  Average CO2 Level: "));
  Serial.print(average);
  Serial.println(F(" ppm"));
  Serial.print(F("[ CO2LV ]  Range: "));
  Serial.print(rangeMin);
  Serial.print(" - ");
  Serial.print(rangeMax);
  Serial.print(F("               |  Status: "));
  Serial.println(statusCO2LV);
}
#endif
