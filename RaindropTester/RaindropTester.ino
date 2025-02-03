#include <rgb_lcd.h>

const int raindropSensor = 7;
const int colorR = 0;
const int colorG = 200;
const int colorB = 0;

rgb_lcd lcd;
bool running = true;

void setup() {
  Serial.begin(9600);

  lcd.begin(16, 2);
  lcd.setRGB(colorR, colorG, colorB);

  pinMode(raindropSensor, INPUT);


}

void loop() {

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

  if (running) {
    lcd.setCursor(0,0);
    lcd.print("Raindrop Reading:");

    lcd.setCursor(0, 1);
    if(digitalRead(raindropSensor) == 1) {
      lcd.print("No Rain                ");
    } else {
      lcd.print("Rain Detected!         ");
    }
    delay(50);
  }
}
