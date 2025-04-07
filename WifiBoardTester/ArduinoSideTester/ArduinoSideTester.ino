#include <SoftwareSerial.h>

SoftwareSerial ESPReceiver(2, 3); // RX, TX
SoftwareSerial ESPTransmitter(4, 5); // RX, TX

void setup() {
    Serial.begin(19200);
    ESPReceiver.begin(4800);   // Lower baud rate for better reliability
    ESPTransmitter.begin(4800);
}

// Function to read data character by character
// void readFromSoftwareSerial(SoftwareSerial &serial, String &output) {
//     while (serial.available()) {
//         char c = serial.read();
//         output += c;
//         delayMicroseconds(500); // Small delay to allow buffer processing
//     }
// }

void loop() {
    if (Serial.available()) {
        String userInput = Serial.readStringUntil('\n');
        userInput.trim();
        if (userInput.startsWith("USER:")) {
            userInput = userInput.substring(5);
            ESPTransmitter.listen();  // Ensure we are listening to the right serial
            delay(5); // Allow time for switching
            ESPTransmitter.println(userInput);
            Serial.println("Sent to ESPTransmitter: " + userInput);
            Serial.println("-------------------------------------");
            delay(500);
        }
    }

    // Read from ESPReceiver
    String receiverData = "";
    ESPReceiver.listen();  // Ensure we are listening to ESPReceiver
    delay(500);
    //readFromSoftwareSerial(ESPReceiver, receiverData);
    receiverData = ESPReceiver.readStringUntil('\n');
    receiverData.trim();
    if (receiverData.length() > 0 && receiverData.startsWith("ESP:")) {
        Serial.println(receiverData);
        Serial.println("-------------------------------------");
    }
}
// #include <SoftwareSerial.h>

// SoftwareSerial mySerial(2, 3); // RX, TX
// bool wifiReceivedSSID = false;
// bool wifiReceivedPassword = false;
// bool wifiReceivedServer = false;

// void setup() {
//     Serial.begin(19200);  // Set baud rate for debugging
//     mySerial.begin(9600);

//     Serial.println("Enter Credentials to Connect to Wifi:");
//     Serial.println("ssid=YourNetworkName");
//     Serial.println("pass=YourNetworkPassword");
//     Serial.println("server=YourServerIp");
// }

// void loop() {
//     if (Serial.available()) {
//       String userInput = Serial.readStringUntil('\n');
//       userInput.trim();

//       if (userInput.startsWith("ssid=") || userInput.startsWith("pass=") || userInput.startsWith("server=")) {
//         mySerial.println(userInput);
//       } else if (userInput.startsWith("HELP")){
//         mySerial.println("HELP");
//       } else if (userInput.startsWith("CONNECT") && wifiReceivedPassword && wifiReceivedServer && wifiReceivedSSID){
//         mySerial.println("CONNECT");
//       }else if (!wifiReceivedPassword || !wifiReceivedSSID || !wifiReceivedServer){
//         Serial.println("--------------------------------------");
//         Serial.println("Please supply the Chip with All These Credentials:");
//         Serial.println("ssid=YourNetworkName");
//         Serial.println("pass=YourNetworkPassword");
//         Serial.println("server=YourServerIp");
//         Serial.println("--------------------------------------");
//         Serial.print("ssid Provided Already: ");
//         Serial.println(wifiReceivedSSID);
//         Serial.print("password Provided Already: ");
//         Serial.println(wifiReceivedPassword);
//         Serial.print("server Provided Already: ");
//         Serial.println(wifiReceivedServer);
//         Serial.println("--------------------------------------");
//       } else if (wifiReceivedPassword && wifiReceivedSSID && wifiReceivedServer && userInput.startsWith("USER:")) {
//         mySerial.println(userInput);
//       }else {
//         Serial.println("To provide input to send to server, put USER: before the message!");
//       }
//     }

//     if (mySerial.available()) {  // Check if data is available on RX
//         String receivedData = mySerial.readStringUntil('\n');  // Read until newline
//         receivedData.trim();  // Remove any whitespace or newline chars
//         Serial.println(receivedData);

//        Serial.println("--------------------------------------");
//        if (receivedData.startsWith("ssidPASS")) {
//         wifiReceivedSSID = true;
//        } else if (receivedData.startsWith("passPASS")) {
//         wifiReceivedPassword = true;
//        } else if (receivedData.startsWith("serverPASS")) {
//         wifiReceivedServer = true;
//        }
//     }
// }