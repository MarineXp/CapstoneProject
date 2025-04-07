#include <WiFi.h>
#include <WebServer.h>

const char* ssid = "ESP-Smart_Irrigation_System";  // AP SSID
const char* password = "PleaseWork123!@#";  // Open network

WebServer server(80);  // HTTP Server on port 80

// Serial1 Communication
#define RX_PIN 16
#define TX_PIN 5
#define BAUD_RATE 4800

void handleIncomingData() {
    if (server.hasArg("plain")) {
        String receivedData = server.arg("plain");
        Serial1.print("ESP:");
        Serial1.println(receivedData);
        String output = "Data: '" + receivedData + "' received successfully";
        server.send(200, "text/plain", output);
    } else {
        server.send(400, "text/plain", "ESPR:No data received");
    }
}

void setup() {
    Serial1.begin(BAUD_RATE, SERIAL_8N1, RX_PIN, TX_PIN);

    // Define a static IP address for the AP
    IPAddress local_IP(192, 168, 4, 1);  // Default ESP AP IP
    IPAddress gateway(192, 168, 4, 1);
    IPAddress subnet(255, 255, 255, 0);

    WiFi.softAPConfig(local_IP, gateway, subnet);
    WiFi.softAP(ssid, password);

    // Start WiFi as an Access Point
    WiFi.softAP(ssid, password);
    Serial1.println("Access Point Created!");
    Serial1.print("AP IP Address: ");
    Serial1.println(WiFi.softAPIP());

    // Setup HTTP POST handler
    server.on("/data", HTTP_POST, handleIncomingData);
    
    server.begin();
    Serial1.println("Server is running...");
}

void loop() {
    server.handleClient();  // Handle incoming HTTP requests
}