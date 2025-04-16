#include <SoftwareSerial.h>

const int solenoidPin = 6;
const int tempPowerPin = 13;
const int leakPin = A0;
const int moistSensor1Pin = A1;
const int moistSensor2Pin = A3;
const int lightPin = A2;
const int tempPin = A4;

String inputBuffer = "";

SoftwareSerial BTSerial(9, 8);

void setup() {
  /*
  Digital & Analog Pin Reference:
  D6 = Solenoid Valve
  D13 = Temp Sensor Power Pin

  A0 = Raindrop Sensor
  A1 = Moist Sensor 1
  A2 = Light Sensor
  A3 = Moist Sensor 2
  A4 = Temp Sensor
  */

  Serial.begin(9600);
  BTSerial.begin(9600);
  pinMode(solenoidPin, OUTPUT);
  pinMode(tempPowerPin, OUTPUT);
  digitalWrite(solenoidPin, LOW);
  digitalWrite(tempPowerPin, HIGH);
}

void loop() {
  while (BTSerial.available() > 0) {
    char incomingChar = BTSerial.read();

    if (incomingChar != '\n') {
      inputBuffer += incomingChar;
    } else {
      inputBuffer.trim();

      commandHandler(inputBuffer);
      inputBuffer = "";
    }
  }

  if ((analogRead(leakPin) * (3.3 / 1023)) < 1.5) {
    BTSerial.println("DATS:LEAK");
  }
  delay(500);
}

void commandHandler(String command) {
  if (command.startsWith("ARCOM:")) {
    command = command.substring(6);
    if (command.equals("OV")) {
      digitalWrite(solenoidPin, HIGH);
      BTSerial.println("ACKS:Valve Open");
    } else if (command.equals("CV")) {
      digitalWrite(solenoidPin, LOW);
      BTSerial.println("ACKS:Valve Closed");
    } else if (command.equals("GM1")) {
      BTSerial.print("DATS:MOIST1:");
      BTSerial.println(analogRead(moistSensor1Pin));
      Serial.println("Sent Moist 1");
      delay(50);
    } else if (command.equals("GM2")) {
      BTSerial.print("DATS:MOIST2:");
      BTSerial.println(analogRead(moistSensor2Pin));
      Serial.println("Sent Moist 2");
      delay(50);
    } else if (command.equals("GL")) {
      BTSerial.print("DATS:LIGHT:");
      BTSerial.println(analogRead(lightPin));
      delay(50);
    } else if (command.equals("GT")) {
      BTSerial.print("DATS:TEMP:");
      BTSerial.println(analogRead(tempPin));
      delay(50);
    }
  }

  delay(1000);
}