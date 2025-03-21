// #include <WiFi.h>
// #include <HTTPClient.h>

// const char* ssid = "TP-Link_E4AE";  // Change this
// const char* password = "28767923";  // Change this
// const char* server = "http://192.168.0.114:8000";  // Replace with your Mac’s IP

// // Define the RX and TX pins
// #define RX_PIN 16
// #define TX_PIN 5

// // Define the baud rate
// #define BAUD_RATE 9600

// void setup() {
//     Serial.begin(19200);  // Start Serial for debugging

//     // Initialize the UART driver
//     Serial1.begin(BAUD_RATE, SERIAL_8N1, RX_PIN, TX_PIN);
//     WiFi.begin(ssid, password);

//     Serial.print("Connecting to WiFi");
//     while (WiFi.status() != WL_CONNECTED) {
//         delay(500);
//         Serial.print(".");
//     }
//     Serial.println("\nConnected to WiFi");
// }

// void loop() {
//     if (Serial1.available()) {  // Check if data is available on RX
//         String receivedData = Serial1.readStringUntil('\n');  // Read until newline
//         receivedData.trim();  // Remove any whitespace or newline chars
//         Serial.print("Received: ");
//         Serial.println(receivedData);
//         if (receivedData.length() > 0) {  // Ensure there's actual data
//             sendToServer(receivedData);  // Send received data to server
//             Serial1.print("Received: ");
//             Serial1.println(receivedData);
//         }
//     } else {
//       sendToServer("Not getting Data Yet!");
//     }
//     delay(1000);  // Send every 1 second
// }

// void sendToServer(String message) {
//     if (WiFi.status() == WL_CONNECTED) {
//         HTTPClient http;
//         http.begin(server);  // Connect to the Python server
//         http.addHeader("Content-Type", "text/plain");

//         int httpResponseCode = http.POST(message);  // Send received data

//         Serial.print("Sent: ");
//         Serial.println(message);
//         Serial.print("HTTP Response code: ");
//         Serial.println(httpResponseCode);

//         http.end();  // Close connection
//     } else {
//         Serial.println("WiFi Disconnected");
//     }
// }
// #include <WiFi.h>
// #include <HTTPClient.h>

// String ssid_str = "";  // Change this
// String password_str = "";  // Change this
// String server_str = "";  // Replace with your Mac’s IP

// bool wifiConnected = false;

// // Define the RX and TX pins
// #define RX_PIN 16
// #define TX_PIN 5

// // Define the baud rate
// #define BAUD_RATE 9600

// void setup() {
//     Serial.begin(19200);  // Start Serial for debugging

//     // Initialize the UART driver
//     Serial1.begin(BAUD_RATE, SERIAL_8N1, RX_PIN, TX_PIN);
    
//     Serial1.println("ESP: Waiting for Wifi Credentials...");

//     while (!wifiConnected) {
//       if (Serial1.available()) {  // Check if data is available on RX
//         String receivedData = "";
//         unsigned long startTime = millis();
        
//         // Read the serial buffer and wait for full message
//         while (millis() - startTime < 200) {  // Wait for more data (200ms timeout)
//             if (Serial1.available()) {
//                 receivedData += (char)Serial1.read();  // Read one character at a time
//             }
//         }

//         receivedData.trim();  // Remove any whitespace or newline chars

//         Serial1.print("Received: ");
//         Serial1.println(receivedData);

//         if (receivedData.startsWith("ssid=")) {
//           ssid_str = receivedData.substring(5);
//           Serial1.print("ssidPASS: ");
//           Serial1.println(ssid_str);
//         } else if (receivedData.startsWith("pass=")) {
//           password_str = receivedData.substring(5);
//           Serial1.print("passPASS: ");
//           Serial1.println(password_str);
//         } else if (receivedData.startsWith("server=")) {
//           String serverMod = "http://" + receivedData.substring(7) + ":8000";
//           server_str = serverMod;
//           Serial1.print("serverPASS: ");
//           Serial1.println(serverMod);
//           Serial1.print("Actual: ");
//           Serial1.println(receivedData.substring(7));
//         } else if (receivedData.startsWith("HELP")){
//           Serial1.println("Creds So Far:");
//           Serial1.print("SSID: ");
//           Serial1.println(ssid_str);
//           Serial1.print("Pass: ");
//           Serial1.println(password_str);
//           Serial1.print("Server: ");
//           Serial1.println(server_str);
//         }else if (receivedData.startsWith("CONNECT")) {
//           if (ssid_str != "" && password_str != "" && server_str != "") {
//             ssid_str.trim();
//             password_str.trim();
//             server_str.trim();

//             Serial1.println("ESP: Attempting to Connect to WIFI...");
//             Serial1.print("SSID Found As: '");
//             Serial1.print(ssid_str);
//             Serial1.println("'");
//             Serial1.print("Password Found As: '");
//             Serial1.print(password_str);
//             Serial1.println("'");

//             char ssid[32], password[32];
//             ssid_str.toCharArray(ssid, sizeof(ssid));
//             password_str.toCharArray(password, sizeof(password));

//             const char* ssid_const = ssid;
//             const char* password_const = password;

//             Serial1.print("Converted SSID: ");
//             Serial1.println(ssid_const);
//             Serial1.print("Converted Pass: ");
//             Serial1.println(password_const);

//             delay(1000);

//             WiFi.begin(ssid_const, password_const);

//             Serial.print("Connecting to WiFi");
//             int attempts = 0;
//             while (WiFi.status() != WL_CONNECTED && attempts < 100) {
//                 delay(500);
//                 Serial1.print("Attempt #");
//                 Serial1.println(attempts);
//                 attempts++;
//             }

//             if (WiFi.status() == WL_CONNECTED) {
//               Serial1.println("\nConnected to WiFi!");
//             } else {
//               Serial1.println("Unable to Connect to WiFi");
//               Serial1.print("WiFi Status: ");
//               Serial1.print(WiFi.status());
//               Serial1.print(". Should be: ");
//               Serial1.println(WL_CONNECTED);
//             }
//             wifiConnected = true;
//           }
//         }
//       }
//       delay(1000);  // Send every 1 second
//     }
// }

// void loop() {
//     if (Serial1.available()) {  // Check if data is available on RX
//         String receivedData = "";
//         unsigned long startTime = millis();
        
//         // Read the serial buffer and wait for full message
//         while (millis() - startTime < 200) {  // Wait for more data (200ms timeout)
//             if (Serial1.available()) {
//                 receivedData += (char)Serial1.read();  // Read one character at a time
//             }
//         }

//         receivedData.trim();  // Remove any whitespace or newline chars

//         Serial1.print("Received: ");
//         Serial1.println(receivedData);

//         // If wifi is connected, send the data to the server
//         if (wifiConnected) {
//           serverHandler(receivedData);
//         } else {
//           Serial1.println("ESP: Wifi Not Connected. Cannot Send Data. Please Restart Device...");
//         }
//     }
//     delay(1000);  // Send every 1 second
// }

// void sendToServer(String message) {
//     if (WiFi.status() == WL_CONNECTED) {
//         HTTPClient http;
//         char server[32];
//         server_str.toCharArray(server, sizeof(server));

//         const char* server_const = server;
//         http.begin(server_const);  // Connect to the Python server
//         http.addHeader("Content-Type", "text/plain");

//         int httpResponseCode = http.POST(message);  // Send received data

//         Serial.print("Sent: ");
//         Serial.println(message);
//         Serial.print("HTTP Response code: ");
//         Serial.println(httpResponseCode);

//         http.end();  // Close connection
//     } else {
//         Serial1.println("ESP: WiFi Disconnected...");
//     }
// }

// void serverHandler(String userData) {
//       if (userData.length() > 0) {  // Ensure there's actual data
//           sendToServer(userData);  // Send received data to server
//       }
// }
#include <WiFi.h>
#include <WebServer.h>

const char* ssid = "SmartIrrigationSystem";  // Change this to your preferred AP name
const char* password = "12345678";  // Set a password or leave empty for open AP
const int channel = 11;
const int minConnection = 0;
const int maxConnection = 3;

// Define the RX and TX pins
#define RX_PIN 16
#define TX_PIN 5

// Define the baud rate
#define BAUD_RATE 9600

String latestData = "No data received yet"; // Stores the latest received data
bool startConnection = false;
unsigned long lastRestart = 0;

WebServer server(80);

void handleRoot() {
    server.send(200, "text/plain", latestData); // Send latest data to connected client
}

void setup() {
    Serial.begin(19200);  // Start Serial for debugging
    Serial1.begin(BAUD_RATE, SERIAL_8N1, RX_PIN, TX_PIN);

    WiFi.disconnect(true, true);
    delay(1000);
    
    // Set up the ESP32 as an Access Point with static IP and increased connections
    IPAddress local_IP(192,168,4,1);
    IPAddress gateway(192,168,4,1);
    IPAddress subnet(255,255,255,0);
    WiFi.softAPConfig(local_IP, gateway, subnet);
    WiFi.mode(WIFI_AP);
    WiFi.softAP(ssid, NULL); // add ,password, channel, minConnection, maxConnection
    WiFi.setSleep(false);
    
    Serial1.println("ESP: Access Point Started");
    Serial1.print("ESP: IP Address: ");
    Serial1.println(WiFi.softAPIP());

    // Start web server
    server.on("/", handleRoot);
    server.begin();
    Serial1.println("ESP: Web server started");
}

void loop() {
    server.handleClient(); // Handle web requests
    
    // Monitor and restart AP if no clients are connected
    if (WiFi.softAPgetStationNum() == 0) { 
        Serial.println("No clients, restarting AP...");
        WiFi.softAP(ssid, password, 6, 0, 8);
    }

    // Periodically restart ESP32 to ensure stability
    if (millis() - lastRestart > 600000) { // Every 10 minutes
        Serial.println("Restarting ESP32...");
        ESP.restart();
    }

    // Print number of connected clients
    Serial.print("Connected Clients: ");
    Serial.println(WiFi.softAPgetStationNum());
    
    if (Serial1.available()) {  // Check if data is available on RX
        latestData = Serial1.readStringUntil('\n');  // Read until newline
        latestData.trim();  // Remove any whitespace or newline chars
        
        if (latestData.startsWith("GETINFO")) {
          Serial1.print("ESP: IP Address: ");
          Serial1.println(WiFi.softAPIP());
        } else {
          Serial1.print("ESP: Received: ");
          Serial1.println(latestData);
        }
    }
    delay(10);  // Shorter delay to prevent network timeouts
}

