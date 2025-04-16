#include <SoftwareSerial.h>

SoftwareSerial BTSerial(9, 8); // RX, TX

float moisture1 = 0; //Scaled value of volumetric water content in soil (percent)
float moisture2 = 0; //Scaled value of volumetric water content in soil (percent)
float lightValue = 0;
float tempValue = 0;

String inputBuffer = "";

int year, month, day, hour, minute, second;

int timeCheck = 120; // Loop runs 120 times (500ms delay per loop) every ~1 minutes
int sensorCheck = 3600; // Loops through 3600 times (500ms delay per loop) every ~30 minutes
bool dayChange = false;
bool beginLog = false;
int logCountDown = 10;

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
      if (inputBuffer.equals("OV") || inputBuffer.equals("CV") || inputBuffer.equals("GM1") || inputBuffer.equals("GM2") 
          || inputBuffer.equals("GL") || inputBuffer.equals("GT")) {
        BTSerial.print("ARCOM:");
        BTSerial.println(inputBuffer);
        delay(50);
        Serial.print("ACKM:Received:");
        Serial.println(inputBuffer);
      }
    }

    if (inputBuffer.startsWith("TIME:")) {
      inputBuffer = inputBuffer.substring(5);
      Serial.print("ACKM:Received:");
      Serial.println(inputBuffer);
      delay(50);
      int prevDay = day;
      parseTimeString(inputBuffer);
      delay(50);
      // Show parsed values (for debugging)
      Serial.print("ACKM:Parsed D&T: ");
      Serial.print(year); Serial.print("-");
      Serial.print(month); Serial.print("-");
      Serial.print(day); Serial.print(" ");
      Serial.print(hour); Serial.print(":");
      Serial.print(minute); Serial.print(":");
      Serial.println(second);
      if (prevDay != day) {
        dayChange = true;
      }
    }
  }

  while (BTSerial.available() > 0) {
    char incomingChar = BTSerial.read();
    
    // Build up the string until newline
    if (incomingChar != '\n') {
      inputBuffer += incomingChar;
    } else {
      inputBuffer.trim(); // Remove whitespace

      // Process the full line
      processBluetoothLine(inputBuffer);

      // Clear for next line
      inputBuffer = "";
    }
  }

  if (dayChange) {
    Serial.print("ACKM:DayChange:");
    Serial.println(dayChange);
    dayChange = false;
  }

  sensorCheck = sensorCheck - 1;
  
  timeCheck = timeCheck - 1;

  if (timeCheck == 0) {
    timeCheck = 120;
    Serial.println("ACKM:TIME");
    delay(50);
  }

  if (sensorCheck == 0) {
    sensorCheck = 3600;
    BTSerial.println("ARCOM:GM1");
    delay(125);
    BTSerial.println("ARCOM:GM2");
    delay(125);
    BTSerial.println("ARCOM:GL");
    delay(125);
    BTSerial.println("ARCOM:GT");
    delay(125);
    beginLog = true;
  } else {
    delay(500);
  }

  if (beginLog) {
    logCountDown  = logCountDown - 1;

    if (logCountDown == 0 && year > 0) {
      Serial.println("ACKM:LOG");
      beginLog = false;
      logCountDown = 10;
    } else if (logCountDown == 0){
      beginLog = false;
      logCountDown = 10;
    }
  }
}

float moistureConvert(String slaveInfo) {
  float rawValue = 0;
  slaveInfo = slaveInfo.substring(7);
  rawValue = slaveInfo.toFloat();
  rawValue = rawValue * (3.3/1024);

  return map(rawValue * 100, 0, 178, 0, 100);
}

float lightConvert(String slaveInfo) {
  float rawValue = 0;
  slaveInfo = slaveInfo.substring(6);
  rawValue = slaveInfo.toInt();
  rawValue = 800 - rawValue;

  return map(rawValue, 400, 800, 0, 100);
}

float tempConvert(String slaveInfo) {
  float rawValue = 0;
  slaveInfo = slaveInfo.substring(5);
  rawValue = slaveInfo.toFloat();
  rawValue = rawValue * (3.3 / 1023.0);

  return ((rawValue * 75.006 - 40) - 107.82);
}

void processBluetoothLine(String outputBuffer) {
  if (outputBuffer.startsWith("ACKS:")) {
    Serial.println(outputBuffer);
  }
  
  if (outputBuffer.startsWith("DATS:")) {
    String slaveData = outputBuffer.substring(5);

    if (slaveData.startsWith("MOIST1:")) {
      moisture1 = moistureConvert(slaveData);

      Serial.print("ACKM:MOIST1:");
      Serial.println(moisture1);
    }

    if (slaveData.startsWith("MOIST2:")) {
      moisture2 = moistureConvert(slaveData);

      Serial.print("ACKM:MOIST2:");
      Serial.println(moisture2);
    }

    if (slaveData.startsWith("LIGHT:")) {
      lightValue = lightConvert(slaveData);

      Serial.print("ACKM:LIGHT:");
      Serial.println(lightValue);
    }

    if (slaveData.startsWith("LEAK")) {
      Serial.println("ACKM:LEAK");
    }

    if (slaveData.startsWith("TEMP:")) {
      tempValue = tempConvert(slaveData);

      Serial.print("ACKM:TEMP:");
      Serial.println(tempValue);
    }
  }
}

void parseTimeString(String timeString) {
  int values[6];
  int lastIndex = 0;
  int idx = 0;

  while (idx < 6) {
    int commaIndex = timeString.indexOf(',', lastIndex);
    if (commaIndex == -1 && idx < 5) return; // Invalid format

    String part = (commaIndex == -1) ? timeString.substring(lastIndex)
                                      : timeString.substring(lastIndex, commaIndex);
    values[idx] = part.toInt();
    lastIndex = commaIndex + 1;
    idx++;
  }

  year = values[0];
  month = values[1];
  day = values[2];
  hour = values[3];
  minute = values[4];
  second = values[5];
}
