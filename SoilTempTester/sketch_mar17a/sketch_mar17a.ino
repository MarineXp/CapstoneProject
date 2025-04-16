const int soilTempPin = A4;
const int soilTempPowerPin = 13;
float soilTemp = 0;

void setup() {
  Serial.begin(9600);
  pinMode(soilTempPowerPin, OUTPUT);
  digitalWrite(soilTempPowerPin, HIGH); // Turn sensor on
}

void loop() {
  int rawValue = analogRead(soilTempPin);
  float voltage = rawValue * (3.3 / 1023.0);
  soilTemp = (voltage * 75.006 - 40) - 107.82; // 175.82 - 68 = ~107.82

  Serial.print("Soil Temp Raw: ");
  Serial.println(rawValue);

  Serial.print("Soil Temp (°F): ");
  Serial.println(soilTemp);

  delay(1000);
}
