#include <WiFi.h>
#include <HTTPClient.h>

const char* ssid = "ESP-Smart_Irrigation_System";  // AP SSID
const char* password = "PleaseWork123!@#";  // Open network

const char* serverIP = "192.168.4.1";  // AP’s default IP
const int serverPort = 80;

#define RX_PIN 16
#define TX_PIN 5
#define BAUD_RATE 4800

void setup() {
    Serial1.begin(BAUD_RATE, SERIAL_8N1, RX_PIN, TX_PIN);

    WiFi.begin(ssid, password);
    Serial1.print("Connecting to AP");

    while (WiFi.status() != WL_CONNECTED) {
        delay(500);
        Serial1.print("%");
    }
    
    Serial1.println("\nConnected to AP!");
}

void sendDataToServer(String data) {
    if (WiFi.status() == WL_CONNECTED) {
        HTTPClient http;
        String serverPath = "http://" + String(serverIP) + "/data";

        http.begin(serverPath);
        http.addHeader("Content-Type", "text/plain");

        int httpResponseCode = http.POST(data);  // Send the data

        Serial1.print("Sent: ");
        Serial1.println(data);
        Serial1.print("Response Code: ");
        Serial1.println(httpResponseCode);

        if (httpResponseCode > 0) {  // If the request was successful
            String response = http.getString();  // Get the response body
            Serial1.print("Response Body: ");
            Serial1.println(response);
        } else {
            Serial1.println("Error: No response received");
        }

        http.end();
    } else {
        Serial1.println("WiFi Disconnected!");
    }
}

void loop() {
    if (Serial1.available()) {
        String receivedData = Serial1.readStringUntil('\n');
        receivedData.trim();
        
        if (receivedData.length() > 0) {
            Serial1.print("Received Through Serial: ");
            Serial1.println(receivedData);
            sendDataToServer(receivedData);
        }
    }
    delay(1000);
}
