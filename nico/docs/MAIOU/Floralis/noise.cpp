#include "noise.h"
#include "globals.h"
#include "lcd.h"

static unsigned int index = 0;
static const int SIZE = AVERAGE_PERIOD;
static float currentValue = 0;      // in dB
static float recentValues[SIZE];
static float average = 0;
static const int rangeMin = 0;
static const int rangeMax = 55;

// Calibration — ADJUST THESE BASED ON YOUR ENVIRONMENT
static const int NOISE_FLOOR = 10;    // ignore tiny fluctuations (electrical noise)
static const int MAX_AMPLITUDE = 350; // max peak-to-peak amplitude during loud sound (e.g., clap)

void initNOISE() {
  #if DEBUG_MODE
  Serial.print(F("[ NOISE ]  Initialising Noise Sensor..........."));
  #endif

  pinMode(soundSensorDO_PIN, INPUT);
  pinMode(soundSensorAO_PIN, INPUT);
  pinMode(soundLED_PIN, OUTPUT);

  #if DEBUG_MODE
  Serial.println(F("  [ OK ]"));
  #endif
}

/*void measureNOISE() {
  // --- Peak-to-Peak Sampling (30 ms window) ---
  unsigned long start = millis();
  int minVal = 1023;
  int maxVal = 0;

  while (millis() - start < 30) {  // 30 ms window (ideal for voice/music)
      int sample = analogRead(soundSensorAO_PIN);
      if (sample < minVal) minVal = sample;
      if (sample > maxVal) maxVal = sample;
  }

  int amplitude = maxVal - minVal;

  // Apply noise floor: ignore insignificant fluctuations
  if (amplitude < NOISE_FLOOR) {
      //amplitude = 0;
  }

  // Map to 0–100 "loudness index" (displayed as relative dB)
  int loudness = map(amplitude, 0, MAX_AMPLITUDE, 0, 100);
  loudness = constrain(loudness, 0, 100);

  currentValue = static_cast<float>(loudness);

  recentValues[index++] = currentValue;
  if (index >= SIZE) index = 0;
}*/

void measureNOISE() {
  // --- Peak-to-Peak Sampling (30 ms window) ---
  unsigned long start = millis();
  int minVal = 1023;
  int maxVal = 0;

  while (millis() - start < 30) {  // 30 ms sampling window
    int sample = analogRead(soundSensorAO_PIN);
    if (sample < minVal) minVal = sample;
    if (sample > maxVal) maxVal = sample;
  }

  int amplitude = maxVal - minVal;

  // Map raw amplitude to loudness (0–100), but only if above noise floor
  int loudness = 0;
  if (amplitude >= NOISE_FLOOR) {
    // Map from [NOISE_FLOOR, MAX_AMPLITUDE] → [0, 100]
    loudness = map(amplitude, NOISE_FLOOR, MAX_AMPLITUDE, 0, 100);
    loudness = constrain(loudness, 0, 100);
  }
  // If below noise floor, loudness remains 0

  currentValue = static_cast<float>(loudness);

  // ONLY STORE MEANINGFUL VALUES (or ensure buffer isn't empty)
  if (loudness > 0 || index == 0) {
    recentValues[index] = currentValue;
    index = (index + 1) % SIZE;  // safer than if-check
  }
  // If loudness == 0 and buffer already has data → skip (don't pollute with silence)
}

void calculateAverageNOISE() {
  float sum = 0;
  int count = (index < SIZE) ? index : SIZE;
  for (int i = 0; i < count; i++) {
    sum += recentValues[i];
  }
  average = (count > 0) ? sum / count : 0;
  calculateStatusNOISE();
}

void calculateStatusNOISE() {
  if (average >= rangeMin && average <= rangeMax) {
    statusNOISE = "GOOD";
  } else if (average < rangeMin) {
    statusNOISE = "POOR";
  } else if (average > rangeMax) {
    statusNOISE = "HIGH";
  }
}

/*void printOnLCDScreenNOISE() {
  printMultipleLineOnLCD("Noise level", String(currentValue) + " dB");
  lcd.setCursor(11, 0);
  lcd.print("|" + statusNOISE);
  lcd.setCursor(11, 1);
  lcd.print("|");
}/**/

void printOnLCDScreenNOISE() {

  clearScreenLCD();

  // line 1
  OpenLCD.write(0xFE);
  OpenLCD.write(0x80);
  OpenLCD.print("Noise level");

  // line 2
  OpenLCD.write(0xFE);
  OpenLCD.write(0xC0);
  OpenLCD.print(String(round(currentValue)) + " dB");

  // column 12 indicator
  OpenLCD.write(0xFE);
  OpenLCD.write(0x8B);   // row 1 col 12
  OpenLCD.write(255);    // full block
  OpenLCD.print(statusNOISE);

  OpenLCD.write(0xFE);
  OpenLCD.write(0xCB);   // row 2 col 12
  OpenLCD.write(255);    // full block
}

#if DEBUG_MODE
void printToConsoleNOISE() {
  Serial.print(F("[ NOISE ]  Current Noise Level: "));
  Serial.print(currentValue);
  Serial.println(F(" dB"));
  Serial.print(F("[ NOISE ]  Average Noise Level: "));
  Serial.print(average);
  Serial.println(F(" dB"));
  Serial.print(F("[ NOISE ]  Range: "));
  Serial.print(rangeMin);
  Serial.print(" - ");
  Serial.print(rangeMax);
  Serial.print(F("                 |  Status: "));
  Serial.println(statusNOISE);
}
#endif

/*
void measureSoundDigital() {
  int value = digitalRead(soundSensorDO_PIN); //Reading data from sensor and storing in variable

  Serial.println(value);

  if (value == 1) {
    if (soundIsOn == true) {
      digitalWrite(soundLED_PIN, LOW);
      soundIsOn = false;
    } else {
      digitalWrite(soundLED_PIN, HIGH);
      soundIsOn = true;
    }
  }
}

void measureSoundAnalog() {
  int value = analogRead(soundSensorAO_PIN);

  Serial.println(value);

  if (value > soundThreshold){
    digitalWrite(soundLED_PIN, HIGH);
  } else {
    digitalWrite(soundLED_PIN, LOW);
  }
}
/**/