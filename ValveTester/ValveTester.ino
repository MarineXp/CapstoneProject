const int solenoidPin = 8; // Define the Arduino pin connected to the relay module's control pin


void setup() {
  Serial.begin(9600);
  pinMode(solenoidPin, OUTPUT); // Set the solenoid pin as output
  digitalWrite(solenoidPin, LOW); // Initial setup to low

}



void loop() {

  // Turn the solenoid valve ON for a set duration

  digitalWrite(solenoidPin, HIGH); 
  Serial.println("On");
  delay(1000); // Delay for 1 second



  // Turn the solenoid valve OFF

  digitalWrite(solenoidPin, LOW); 
  Serial.println("Off");
  delay(1000); // Delay for 1 second

}

