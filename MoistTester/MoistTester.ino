bool running = true; // Flag to control the loop
float soilMoistureRaw = 0; //Raw analog input of soil moisture sensor (volts)
float moisture = 0; //Scaled value of volumetric water content in soil (percent)
float soilMoisture = 0;

const int moisturePin = A5;

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
    soilMoistureRaw = analogRead(moisturePin)*(3.3/1024);
    
    Serial.print("Raw Moisture: ");
    Serial.println(soilMoistureRaw);
    delay(20);

    // Map the raw value (0.0 to 3.2V) to moisture percentage (0 to 100%)
    moisture = map(soilMoistureRaw * 100, 0, 178, 0, 100);
    Serial.print("Moisture %: ");
    Serial.println(moisture);


    delay(500);
  }
}
