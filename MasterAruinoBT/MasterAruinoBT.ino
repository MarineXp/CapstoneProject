#include <SoftwareSerial.h>

SoftwareSerial BTSerial(9, 8); // RX, TX

void setup() {
  Serial.begin(9600);
  BTSerial.begin(9600);
  Serial.println("ACKM:Ready to receive commands...");
}

void loop() {
  if (Serial.available() > 0) {
    String inputBuffer = Serial.readStringUntil("\n");
    inputBuffer.trim();

    if (inputBuffer.startsWith("COM:")) {
      inputBuffer = inputBuffer.substring(4);
      if (inputBuffer.equals("OV") || inputBuffer.equals("CV")) {
        BTSerial.print("ARCOM:");
        BTSerial.println(inputBuffer);
        delay(50);
        Serial.print("ACKM:Received:");
        Serial.println(inputBuffer);
      } else if (inputBuffer.equals("GM")) {
        Serial.print("ACKM:Received:");
        Serial.println(inputBuffer);
        delay(50);
        int randomMoist = random(0, 101);
        Serial.print("ACKM:MOIST1:");
        Serial.println(randomMoist);
      }
    }
  }

  if (BTSerial.available() > 0) {
    String outputBuffer = BTSerial.readStringUntil("\n");
    outputBuffer.trim();

    if (outputBuffer.startsWith("ACKS:")) {
      Serial.println(outputBuffer);
    }
  }
  delay(500);
}
