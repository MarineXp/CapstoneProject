const int soilTempPin = A5;
float soilTemp = 0; //Scaled value of soil temp (degrees F)

void setup() {
  //Initialize serial connection
  Serial.begin(9600); //Just for testing

}

void loop() {
  //Collect Variables
  soilTemp = (133.94 * analogRead(soilTempPin) * (3.3 / 1024)) - 82;
  delay(1000);

  Serial.print("Soil Temp Raw: ");
  Serial.println(analogRead(soilTempPin));

  Serial.print("Soil Temp: ");
  Serial.println(soilTemp);

}
