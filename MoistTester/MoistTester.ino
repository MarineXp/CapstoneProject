bool running = true; // Flag to control the loop
float soilMoistureRaw1 = 0; //Raw analog input of soil moisture sensor (volts)
float moisture1 = 0; //Scaled value of volumetric water content in soil (percent)
float soilMoisture1 = 0;
float soilMoistureRaw2 = 0; //Raw analog input of soil moisture sensor (volts)
float moisture2 = 0; //Scaled value of volumetric water content in soil (percent)
float soilMoisture2 = 0;

const int moisturePin1 = A1;
const int moisturePin2 = A3;

void setup() {
  // Initialize serial connection
  Serial.begin(9600);
  

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
    soilMoistureRaw1 = analogRead(moisturePin1)*(3.3/1024);
    soilMoistureRaw2 = analogRead(moisturePin2)*(3.3/1024);
    
    Serial.print("Raw Moisture1: ");
    Serial.println(soilMoistureRaw1);
    Serial.print("Raw Moisture2: ");
    Serial.println(soilMoistureRaw2);
    delay(20);

    // Map the raw value (0.0 to 3.2V) to moisture percentage (0 to 100%)
    moisture1 = map(soilMoistureRaw1 * 100, 0, 178, 0, 100);
    moisture2 = map(soilMoistureRaw2 * 100, 0, 178, 0, 100);
    Serial.print("Moisture1 %: ");
    Serial.println(moisture1);
    Serial.print("Moisture2 %: ");
    Serial.println(moisture2);


    delay(1000);
  }
}
