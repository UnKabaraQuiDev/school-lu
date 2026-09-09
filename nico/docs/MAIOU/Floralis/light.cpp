#include "light.h"
#include "globals.h"
#include "lcd.h"

static unsigned int index = 0;
static const int SIZE = AVERAGE_PERIOD;
static float currentValue = 0;      // in lux
static float recentValues[SIZE];
static float average = 0;
static const int rangeMin = 100;
static const int rangeMax = 700;

void initLIGHT() {
  #if DEBUG_MODE
  Serial.print(F("[ LIGHT ]  Initialising Light Sensor..........."));
  #endif

  pinMode(photoResistorAO_PIN, INPUT);

  #if DEBUG_MODE
  Serial.println(F("  [ OK ]"));
  #endif
}

void measureLIGHT() {
  int rawValue = analogRead(photoResistorAO_PIN);
  currentValue = 48200 * pow(rawValue, -1.53);
  recentValues[index++] = currentValue;
  if (index >= SIZE) index = 0;
}

void calculateAverageLIGHT() {
  float sum = 0;
  int count = (index < SIZE) ? index : SIZE;
  for (int i = 0; i < count; i++) {
    sum += recentValues[i];
  }
  average = (count > 0) ? sum / count : 0;
  calculateStatusLIGHT();
}

void calculateStatusLIGHT() {
  if (average >= rangeMin && average <= rangeMax) {
    statusLIGHT = "GOOD";
  } else if (average < rangeMin) {
    statusLIGHT = "POOR";
  } else if (average > rangeMax) {
    statusLIGHT = "HIGH";
  }
}

/*void printOnLCDScreenLIGHT() {
  printMultipleLineOnLCD("Light level", String(round(currentValue)) + " lux");
  lcd.setCursor(11, 0);
  lcd.print("|" + statusLIGHT);
  lcd.setCursor(11, 1);
  lcd.print("|");
}/**/

void printOnLCDScreenLIGHT() {

  clearScreenLCD();

  // line 1
  OpenLCD.write(0xFE);
  OpenLCD.write(0x80);
  OpenLCD.print("Light level");

  // line 2
  OpenLCD.write(0xFE);
  OpenLCD.write(0xC0);
  OpenLCD.print(String(round(currentValue)) + " lux");

  // column 12 indicator
  OpenLCD.write(0xFE);
  OpenLCD.write(0x8B);   // row 1 col 12
  OpenLCD.write(255);    // full block
  OpenLCD.print(statusLIGHT);

  OpenLCD.write(0xFE);
  OpenLCD.write(0xCB);   // row 2 col 12
  OpenLCD.write(255);    // full block
}

#if DEBUG_MODE
void printToConsoleLIGHT() {
  Serial.print(F("[ LIGHT ]  Current Light Level: "));
  Serial.print(currentValue);
  Serial.println(F(" lux"));
  Serial.print(F("[ LIGHT ]  Average Light Level: "));
  Serial.print(average);
  Serial.println(F(" lux"));
  Serial.print(F("[ LIGHT ]  Range: "));
  Serial.print(rangeMin);
  Serial.print(" - ");
  Serial.print(rangeMax);
  Serial.print(F("              |  Status: "));
  Serial.println(statusLIGHT);
}
#endif

/*
void measureLightLDR() {
  // (value between 0 and 1023)
  int analogValue = analogRead(photoResistorAO_PIN);

  Serial.print(F("Analog reading: "));
  Serial.print(analogValue);

  if (analogValue < 10) {
    Serial.println(F(" - Dark"));
  } else if (analogValue < 200) {
    Serial.println(F(" - Dim"));
  } else if (analogValue < 500) {
    Serial.println(F(" - Light"));
  } else if (analogValue < 800) {
    Serial.println(F(" - Bright"));
  } else {
    Serial.println(F(" - Very bright"));
  }

  int analogValue2 = analogRead(photoResistorAO_PIN2);
  Serial.print(F("Analog reading: "));
  Serial.print(analogValue2);
  if (analogValue2 < 10) {
    Serial.println(F(" - Dark"));
  } else if (analogValue2 < 200) {
    Serial.println(F(" - Dim"));
  } else if (analogValue2 < 500) {
    Serial.println(F(" - Light"));
  } else if (analogValue2 < 800) {
    Serial.println(F(" - Bright"));
  } else {
    Serial.println(F(" - Very bright"));
  }
}

void initLightSensor() {
  pinMode(photoResistorAO_PIN, INPUT);
  pinMode(photoResistorAO_PIN2, INPUT);
  //pinMode(lightSensorSDA_PIN, INPUT);
  //pinMode(lightSensorSCL_PIN, INPUT);

  Wire.begin();
  lightMeter1.begin();
  lightMeter2.begin();
}

void measureLightSensor() {
  float lux1 = lightMeter1.readLightLevel();
  float lux2 = lightMeter2.readLightLevel();
  Serial.print(F("Light (Sensor 1): "));
  Serial.print(lux1);
  Serial.println(F(" lx"));
  Serial.print(F("Light (Sensor 2): "));
  Serial.print(lux2);
  Serial.println(F(" lx"));
}
/**/