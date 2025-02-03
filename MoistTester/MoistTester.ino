#include <rgb_lcd.h>

bool running = true; // Flag to control the loop
float moistureRaw = 0; //Raw analog input of soil moisture sensor (volts)
float moisture = 0; //Scaled value of volumetric water content in soil (percent)
rgb_lcd lcd;

const int colorR = 0;
const int colorG = 200;
const int colorB = 0;
const int moisturePin = A0;

void setup() {
  // Initialize serial connection
  Serial.begin(9600);

  lcd.begin(16, 2);

  lcd.setRGB(colorR, colorG, colorB);

  /*
  Soil Moisture Reference:
  Air = 0%
  Really dry soil = 10%
  Probably as low as you'd want = 20%
  Well watered = 50%
  Cup of water = 100%
  */
}

void loop() {

  lcd.setCursor(0, 0);
  // Check for incoming serial data
  if (Serial.available() > 0) {
    String command = Serial.readStringUntil('\n'); // Read until newline character
    command.trim(); // Remove any extra whitespace

    // If the command is "stop", set running to false
    if (command.equalsIgnoreCase("stop")) {
      running = false;
      Serial.println("Stopping moisture readings...");
    }
    // If the command is "start", resume readings
    else if (command.equalsIgnoreCase("start")) {
      running = true;
      Serial.println("Resuming moisture readings...");
    }
    else {
      lcd.print(command + "                    ");
    }
  }

  // If running flag is true, continue reading temperature
  if (running) {
    // Getting the raw value of the moisture pin (volts)
    moistureRaw = analogRead(moisturePin)*(5.0/1024);
    delay(20);

    lcd.setCursor(0, 0);
    lcd.print("Raw: ");
    lcd.print(moistureRaw);

    // Map the raw value (0.0 to 3.2V) to moisture percentage (0 to 100%)
    moisture = map(moistureRaw * 100, 0, 354, 0, 100);

    lcd.setCursor(0, 1);
    lcd.print("Moisture %:");
    lcd.print(moisture);
  }
}
