// Libraries
// (also included in other files)

#include <Arduino.h>
//#include <LiquidCrystal.h>      // for lcd.h
#include <SoftwareSerial.h>     // for lcd.h
#include <SensirionI2cScd4x.h>  // for co2.h
#include <Wire.h>               // for co2.h
#include <DHT.h>                // for temperature.h

// Files

#include "globals.h"
#include "lcd.h"
#include "co2.h"              // CO2LV
#include "humidity.h"         // HUMID
#include "light.h"            // LIGHT
#include "noise.h"            // NOISE
#include "temperature.h"      // TEMPR

void setup() {

  // --- Serial Communication ---

  Serial.begin(115200);
  Serial.println(F(""));
  Serial.println(F("--- Setting up ... ---"));

  // --- Initialisation ---

  // LCD Display
  initLCD();
  clearScreenLCD();
  changeColorLCD("blue");
  //printMultipleLineOnLCD("[Sensor]", "[Value]");

  /*lcd.setCursor(11, 0);
  lcd.print("|OKOR");
  lcd.setCursor(11, 1);
  lcd.print("|NOT");*/

  // Sensors
  initCO2LV();
  initHUMID();
  initLIGHT();
  initNOISE();
  initTEMPR();

  // --- Setup complete ---
  
  Serial.println(F("--- Setup complete ---"));
  changeColorLCD("white");
}

void loop() {
  unsigned long now = millis();
  measureALL(now);
  calculateAverageALL(now);
  toggleLCDScreenData(now);

  #if DEBUG_MODE
  printToConsoleALL(now);
  #endif
  delay(10);
}

void measureALL(long now) {
  if (now - lastCO2LV >= INTERVAL_MEASURE_CO2LV) {
    measureCO2LV();
    lastCO2LV = now;
  }
  if (now - lastHUMID >= INTERVAL_MEASURE_HUMID) {
    measureHUMID();
    lastHUMID = now;
  }
  if (now - lastLIGHT >= INTERVAL_MEASURE_LIGHT) {
    measureLIGHT();
    lastLIGHT = now;
  }
  if (now - lastNOISE >= INTERVAL_MEASURE_NOISE) {
    measureNOISE();
    lastNOISE = now;
  }
  if (now - lastTEMPR >= INTERVAL_MEASURE_TEMPR) {
    measureTEMPR();
    lastTEMPR = now;
  }

  /*
  if (now - lastHUMID >= INTERVAL_MEASURE_HUMID && toggleHUMIDorTEMPR) {
    measureHUMID();
    lastHUMID = now;
    lastTEMPR = now;
    toggleHUMIDorTEMPR = !toggleHUMIDorTEMPR;
  } else if (now - lastTEMPR >= INTERVAL_MEASURE_TEMPR && !toggleHUMIDorTEMPR) {
    measureTEMPR();
    lastHUMID = now;
    lastTEMPR = now;
    toggleHUMIDorTEMPR = !toggleHUMIDorTEMPR;
  }
  /**/

  /*
  if (now - lastHUMID >= INTERVAL_MEASURE_HUMID) {
    float h = dht.readHumidity();
    float t = dht.readTemperature();
    #if DEBUG_MODE
    if (isnan(h)) {
      Serial.println(F("[ HUMID ]  Failed to read from DHT sensor!"));
    }
    if (isnan(t)) {
      Serial.println(F("[ TEMPR ]  Failed to read from DHT sensor!"));
    }
    #endif

    if (!isnan(h)) {
      updateHUMID(h);
    }
    delay(100);
    if (!isnan(t)) {
      updateTEMPR(t);
    }

    lastHUMID = now;
    lastTEMPR = now;
  }
  /**/
}

void calculateAverageALL(long now) {
  if (now - lastAVERAGE >= INTERVAL_CALCULATE_AVERAGE) {
    calculateAverageCO2LV();
    calculateAverageHUMID();
    calculateAverageLIGHT();
    calculateAverageNOISE();
    calculateAverageTEMPR();

    updateColor();

    lastAVERAGE = now;
  }
}

void toggleLCDScreenData(long now) {
  static int index = 0;
  if (now - lastTOGGLE >= INTERVAL_TOGGLE_LCD_SCREEN) {
    index++;
    if (index > 4) index = 0;
    lastTOGGLE = now;
    switch(index) {
      case 0: printOnLCDScreenCO2LV(); break;
      case 1: printOnLCDScreenHUMID(); break;
      case 2: printOnLCDScreenLIGHT(); break;
      case 3: printOnLCDScreenNOISE(); break;
      case 4: printOnLCDScreenTEMPR(); break;
    }
  }
}

void updateColor() {
  int count = 0;
  if (statusCO2LV.equals("GOOD")) count++;
  if (statusHUMID.equals("GOOD")) count++;
  if (statusLIGHT.equals("GOOD")) count++;
  if (statusNOISE.equals("GOOD")) count++;
  if (statusTEMPR.equals("GOOD")) count++;
  count = 4;
  
  if (count >= 4) {
    rgbLEDstatus = 0;
    changeColorLCD("green");
  } else if (count >= 2) {
    rgbLEDstatus = 1;
    changeColorLCD("yellow");
  } else {
    rgbLEDstatus = 2;
    changeColorLCD("red");
  }
}

#if DEBUG_MODE
void printToConsoleALL(long now) {
  if (now - lastCONSOLE >= INTERVAL_PRINT_TO_CONSOLE) {
    printToConsoleCO2LV();
    printToConsoleHUMID();
    printToConsoleLIGHT();
    printToConsoleNOISE();
    printToConsoleTEMPR();
    lastCONSOLE = now;
  }
}
#endif
