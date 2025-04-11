/*
  SD card datalogger

  This example shows how to log data from three analog sensors
  to an SD card using the SD library. Pin numbers reflect the default
  SPI pins for Uno and Nano models

  The circuit:
   analog sensors on analog pins 0, 1, and 2
   SD card attached to SPI bus as follows:
 ** SDO - pin 11
 ** SDI - pin 12
 ** CLK - pin 13
 ** CS - depends on your SD card shield or module.
 		Pin 10 used here for consistency with other Arduino examples
    (for MKR Zero SD: SDCARD_SS_PIN)

  created  24 Nov 2010
  modified  24 July 2020
  by Tom Igoe

  This example code is in the public domain.

*/

#include <SPI.h>
#include <SD.h>

const int chipSelect = 10;
const int maxOscillations = 4; // Number of oscillations before stopping
int oscillationCount = 0;
int countUp = 0;
int countDown = 75;
bool ascending = true;
bool countingDown = true;

void setup() {
  Serial.begin(9600);
  while (!Serial);

  Serial.print("Initializing SD card...");
  if (!SD.begin(chipSelect)) {
    Serial.println("initialization failed. Check wiring and SD card.");
    while (true);
  }
  Serial.println("initialization done.");
}

void loop() {
  if (oscillationCount >= maxOscillations) {
    Serial.println("Finished logging.");
    while (true); // Stop execution
  }

  // Create data string
  String dataString = String(countUp) + "," + String(countDown) + ",100";

  // Open file and write data
  File dataFile = SD.open("datalog.txt", FILE_WRITE);
  if (dataFile) {
    dataFile.println(dataString);
    dataFile.close();
    Serial.println(dataString);
  } else {
    Serial.println("error opening datalog.txt");
  }

  // Update oscillating values
  if (ascending) {
    countUp++;
    if (countUp > 50) {
      countUp = 50;
      ascending = false;
    }
  } else {
    countUp--;
    if (countUp < 0) {
      countUp = 0;
      ascending = true;
      oscillationCount++; // Count full oscillation
    }
  }

  if (countingDown) {
    countDown--;
    if (countDown < 25) {
      countDown = 25;
      countingDown = false;
    }
  } else {
    countDown++;
    if (countDown > 75) {
      countDown = 75;
      countingDown = true;
    }
  }

  delay(100); // Adjust delay to control logging speed
}
