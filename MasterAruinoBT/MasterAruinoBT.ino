#include <SoftwareSerial.h>

SoftwareSerial BTSerial(9, 8); // RX, TX

float soilMoistureRaw1 = 0; //Raw analog input of soil moisture sensor (volts)
float moisture1 = 0; //Scaled value of volumetric water content in soil (percent)
float soilMoistureRaw2 = 0; //Raw analog input of soil moisture sensor (volts)
float moisture2 = 0; //Scaled value of volumetric water content in soil (percent)
int lightValueRaw = 0;
int lightValue = 0;

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
        BTSerial.print("ARCOM:");
        BTSerial.println(inputBuffer);
        delay(50);
        Serial.print("ACKM:Received:");
        Serial.println(inputBuffer);
      } else if (inputBuffer.equals("GL")) {
        BTSerial.println("ARCOM:");
        BTSerial.println(inputBuffer);
        delay(50);
        Serial.print("ACKM:Received:");
        Serial.println(inputBuffer);
      }
    }
  }

  if (BTSerial.available() > 0) {
    String outputBuffer = BTSerial.readStringUntil("\n");
    outputBuffer.trim();

    if (outputBuffer.startsWith("ACKS:") || outputBuffer.startsWith("DATS:")) {
      Serial.println(outputBuffer);
    }
    
    if (outputBuffer.startsWith("DATS:")) {
      String slaveData = outputBuffer.substring(5);

      if (slaveData.startsWith("MOIST1:")) {
        slaveData = slaveData.substring(7);
        soilMoistureRaw1 = slaveData.toFloat();
        moisture1 = map(soilMoistureRaw1 * 100, 0, 178, 0, 100);

        Serial.print("ACKM:MOIST1:");
        Serial.println(moisture1);
      }

      if (slaveData.startsWith("MOIST2:")) {
        slaveData = slaveData.substring(7);
        soilMoistureRaw2 = slaveData.toFloat();
        moisture2 = map(soilMoistureRaw2 * 100, 0, 178, 0, 100);

        Serial.print("ACKM:MOIST2:");
        Serial.println(moisture2);
      }

      if (slaveData.startsWith("LIGHT:")) {
        slaveData = slaveData.substring(6);
        lightValueRaw = slaveData.toInt();
        lightValue = map(lightValueRaw, 0, 800, 0, 100);

        Serial.print("ACKM:LIGHT:");
        Serial.println(lightValue);
      }

      if (slaveData.startsWith("LEAK")) {
        Serial.println("ACKM:LEAK");
      }
    }
  }
  delay(500);
}
