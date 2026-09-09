#include "lcd.h"
#include "globals.h"

/*void initLCD() {
  #if DEBUG_MODE
  Serial.print(F("[ LCD   ]  Initialising LCD Display............"));
  #endif

  pinMode(rsPIN, OUTPUT);
  pinMode(enPIN, OUTPUT);
  pinMode(d4PIN, OUTPUT);
  pinMode(d5PIN, OUTPUT);
  pinMode(d6PIN, OUTPUT);
  pinMode(d7PIN, OUTPUT);
  //pinMode(lcdBacklight_PIN, OUTPUT);

  lcd.begin(16, 2);
  lcd.clear();
  lcd.setCursor(0, 0);

  #if DEBUG_MODE
  Serial.println(F("  [ OK ]"));
  #endif
}/**/

void initLCD() {
  #if DEBUG_MODE
  Serial.print(F("[ LCD   ]  Initialising LCD Display............"));
  #endif

  OpenLCD.begin(9600);
  setContrast(2);
  clearScreenLCD();

  delay(1000);

  changeColorLCD("blue");
  printMultipleLineOnLCD("[Sensor]", "[Value]");

  #if DEBUG_MODE
  Serial.println(F("  [ OK ]"));
  #endif
}

void setContrast(int contrastValue) {
  OpenLCD.write(0xFE);            // Beginning of a new command (Settings Mode)
  OpenLCD.write(24);             // Send contrast command
  OpenLCD.write(contrastValue);  // Set contrast value [0;255]
}

/*void printMultipleLineOnLCD(String line1, String line2) {
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print(line1);
  lcd.setCursor(0, 1);
  lcd.print(line2);
}/**/

/*void printMultiLine(String line1, String line2) {
  clearScreenLCD();
  OpenLCD.setCursor(0, 0);
  OpenLCD.print(line1);
  OpenLCD.setCursor(0, 1);
  OpenLCD.print(line2);
}/**/

void printMultipleLineOnLCD(String line1, String line2) {
  clearScreenLCD();
  // Set cursor to the 1st row
  OpenLCD.write(0xFE);  // Control character for command mode
  OpenLCD.write(0x80);  // Position: 1st row, 1st column
  OpenLCD.print(line1);

  // Set cursor to the 2nd row
  OpenLCD.write(0xFE);  // Control character for command mode
  OpenLCD.write(0xC0);  // Position: 2nd row, 1st column
  OpenLCD.print(line2);
}

/*void clearScreenLCD() {
  lcd.clear();
}/**/

/*void clearScreenLCD() {
  OpenLCD.write('|');  // Beginning of a new command (Settings Mode)
  OpenLCD.write('-');  // Clear screen and reset cursor position
}/**/

/*void clearScreenLCD() {
  OpenLCD.write(0xFE);  // Beginning of a new command (Settings Mode)
  OpenLCD.write(0x01);  // Clear screen and reset cursor position
}/**/

void clearScreenLCD() {
  OpenLCD.print("                                ");
  //printMultipleLineOnLCD("                ", "                ");
}

/*void dimBackgroundLightLCD(int percent) {
  if (percent >= 0 && percent <= 100) {
    analogWrite(lcdBacklight_PIN, 255 * percent / 100);
  }
}/**/

void changeColorLCD2(String color) {
  if (color.equals("blue")) {
    Serial.println(F("----------------- blue --------------------"));
    /*OpenLCD.write(0xFE);       // Put LCD into setting mode
    OpenLCD.write(188 + 29);  // Blue
    OpenLCD.write(0xFE);       // Put LCD into setting mode
    OpenLCD.write(128 + 0);   // Red
    OpenLCD.write(0xFE);       // Put LCD into setting mode
    OpenLCD.write(158 + 0);   // Green*/
    OpenLCD.write('|'); OpenLCD.write('+'); OpenLCD.write(128 + 0); // red
    OpenLCD.write('|'); OpenLCD.write(','); OpenLCD.write(158 + 0); // green
    OpenLCD.write('|'); OpenLCD.write('-'); OpenLCD.write(188 + 29); // blue
  } else if (color.equals("green")) {
    Serial.println(F("----------------- green --------------------"));
    OpenLCD.write(0xFE);       // Put LCD into setting mode
    OpenLCD.write(188 + 0);   // Blue
    OpenLCD.write(0xFE);       // Put LCD into setting mode
    OpenLCD.write(158 + 29);  // Green
    OpenLCD.write(0xFE);       // Put LCD into setting mode
    OpenLCD.write(128 + 0);   // Red
  } else if (color.equals("yellow")) {
    Serial.println(F("----------------- yellow --------------------"));
    OpenLCD.write(0xFE);       // Put LCD into setting mode
    OpenLCD.write(188 + 0);   // Blue
    OpenLCD.write(0xFE);       // Put LCD into setting mode
    OpenLCD.write(158 + 15);  // Green
    OpenLCD.write(0xFE);       // Put LCD into setting mode
    OpenLCD.write(128 + 15);  // Red
  } else if (color.equals("red")) {
    Serial.println(F("----------------- red --------------------"));
    OpenLCD.write(0xFE);       // Put LCD into setting mode
    OpenLCD.write(188 + 0);   // Blue
    OpenLCD.write(0xFE);       // Put LCD into setting mode
    OpenLCD.write(158 + 0);   // Green
    OpenLCD.write(0xFE);       // Put LCD into setting mode
    OpenLCD.write(128 + 29);  // Red
  } else {
    return;
  }
}

void changeColorLCD(String color) {
  byte red = 0;
  byte green = 0;
  byte blue = 0;

  if (color.equals("blue")) {
    blue = 29;
  } else if (color.equals("green")) {
    green = 29;
  } else if (color.equals("yellow")) {
    red = 15;
    green = 15;
  } else if (color.equals("red")) {
    red = 29;
  } else if (color.equals("white")) {
    red = 29;
    green = 29;
    blue = 29;
  } else {
    return;
  }

  OpenLCD.write('|');
  OpenLCD.write(128 + red);    // Red: 128-157

  OpenLCD.write('|');
  OpenLCD.write(158 + green);  // Green: 158-187

  OpenLCD.write('|');
  OpenLCD.write(188 + blue);   // Blue: 188-217

  delay(5);
}
