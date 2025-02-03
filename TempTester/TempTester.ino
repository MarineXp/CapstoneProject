#include <OneWire.h>
#include <DallasTemperature.h>
#include <rgb_lcd.h>
#include <Wire.h>
#define ONE_WIRE_BUS 7 // ds18b20 module attached to pin 7
OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);


bool running = true; // Flag to control the loop
rgb_lcd lcd;

const int colorR = 0;
const int colorG = 200;
const int colorB = 0;

void setup() {
  // Initialize serial connection
  Serial.begin(9600);
  sensors.begin(); // Initialize the bus

  lcd.begin(16, 2);

  lcd.setRGB(colorR, colorG, colorB);

  delay(1000);
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
      Serial.println("Stopping temperature readings...");
    }
    // If the command is "start", resume readings
    else if (command.equalsIgnoreCase("start")) {
      running = true;
      Serial.println("Resuming temperature readings...");
    }
    else {
      lcd.print(command + "                    ");
    }
  }

  // If running flag is true, continue reading temperature
  if (running) {
    Serial.println("Requesting Temperatures...");
    sensors.requestTemperatures();
    
    // Get and display temperature
    float tempF = 1.8 * sensors.getTempCByIndex(0) + 32.0;
    //Serial.print("Temperature: ");
    //Serial.print(tempF);
    //Serial.println(" F");

    lcd.setCursor(0, 1);
    lcd.print("F: ");
    lcd.print(tempF);
    
    // Print temperature for plotting
    Serial.println(tempF); // Plotter uses this line for plotting
    
    delay(500); // Adjust the delay as needed
  }
}
