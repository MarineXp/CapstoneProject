#include <rgb_lcd.h>

const int colorR = 0;
const int colorG = 200;
const int colorB = 0;

rgb_lcd lcd;
bool running = true;

void setup() {
  Serial.begin(9600);

  lcd.begin(16, 2);
  lcd.setRGB(colorR, colorG, colorB);

}

void loop() {

  // Check for incoming serial data
  if (Serial.available() > 0) {
    String command = Serial.readStringUntil('\n'); // Read until newline character
    command.trim(); // Remove any extra whitespace

    // If the command is "stop", set running to false
    if (command.equalsIgnoreCase("stop")) {
      running = false;
      Serial.println("Stopping light readings...");
    }
    // If the command is "start", resume readings
    else if (command.equalsIgnoreCase("start")) {
      running = true;
      Serial.println("Resuming light readings...");
    }
  }

  if (running) {
    int lightValue = analogRead(A0);
    delay(20);

    lightValue = map(lightValue, 0, 800, 0, 100);

    lcd.setCursor(0, 0);
    lcd.print("Light Reading:");

    if(lightValue < 100) {
      lcd.setCursor(0, 1);
      lcd.print(lightValue);
      lcd.setCursor(2, 1);
      lcd.print("%");
    } else {
    lcd.setCursor(0, 1);
    lcd.print(lightValue);
    lcd.setCursor(3, 1);
    lcd.print("%");
    }
    delay(100);
  }
}
