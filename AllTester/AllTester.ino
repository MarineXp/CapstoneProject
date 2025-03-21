#include <OneWire.h>
#include <DallasTemperature.h>
#include <Wire.h>
#define ONE_WIRE_BUS 7 // ds18b20 module attached to pin 7
OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);

bool running = true; // Flag to control the loop
float moistureRaw = 0; //Raw analog input of soil moisture sensor (volts)
float moisture = 0; //Scaled value of volumetric water content in soil (percent)
rgb_lcd lcd;

const int colorR = 0;
const int colorG = 200;
const int colorB = 0;
const int moisturePin = A0;
const int lightPin = A1;
const int raindropSensor = 6;

void setup() {
  // Initialize serial connection
  Serial.begin(9600);

  lcd.begin(16, 2);

  lcd.setRGB(colorR, colorG, colorB);

  pinMode(raindropSensor, INPUT);

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
  }

  // If running flag is true, continue reading temperature
  if (running) {
    // Getting the raw value of the moisture pin (volts)
    moistureRaw = analogRead(moisturePin)*(5.0/1024);
    int lightValue = analogRead(lightPin);
    sensors.requestTemperatures();
    delay(20);
    
    // Map the raw value (0.0 to 3.2V) to moisture percentage (0 to 100%)
    moisture = map(soilMoistureRaw * 100, 0, 178, 0, 100);
    lightValue = map(lightValue, 0, 800, 0, 100);

    lcd.setCursor(0, 0);
    lcd.print("M: ");
    lcd.setCursor(3, 0);

    if(moisture >= 100) {
      lcd.print(int(moisture));
      lcd.setCursor(6, 0);
      lcd.print("%");
    } 
    else if(moisture >= 10) {
      lcd.print(int(moisture));
      lcd.setCursor(5, 0);
      lcd.print("% ");
    } else {
      lcd.print(int(moisture));
      lcd.setCursor(4, 0);
      lcd.print("%  ");
    }

    lcd.setCursor(9, 0);
    lcd.print("R: ");
    lcd.setCursor(12, 0);

    if(digitalRead(raindropSensor) == 1) {
      lcd.print("No            ");
    } else {
      lcd.print("Rain!         ");
    }

    lcd.setCursor(0, 1);
    lcd.print("L: ");
    lcd.setCursor(3, 1);

    if(lightValue < 100) {
      lcd.print(lightValue);
      lcd.setCursor(5, 1);
      lcd.print("% ");
    } else {
      lcd.print(lightValue);
      lcd.setCursor(6, 1);
      lcd.print("%");
    }

    lcd.setCursor(9, 1);
    lcd.print("F: ");
    lcd.setCursor(11, 1);
    lcd.print(tempF);

    delay(200);
  }
}
