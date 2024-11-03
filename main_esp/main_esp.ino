// #include <NTPClient.h>

#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>
#include <WebServer.h>
#include "time.h"

// WiFi configuration
const char* ssid = "TP-LINK_E308";
const char* password = "99990000";
// const char* ssid = "iPhonee";
// const char* password = "1234567890";

// API endpoint
const char* API_URL = "http://192.168.136.103:8080/api/logs";
const char* DEFAULT_KEY_URL = "http://192.168.136.103:8080/api/default_key";
const char* VALID_CARD_ID_URL = "http://192.168.136.103:8080/api/valid_card_id";
// const char* DOOR_CONTROL_URL = "http://192.168.136.103:8080/api/control-door";

// Time configuration
const char* ntpServer = "pool.ntp.org";
const long gmtOffset_sec = 0;
const int daylightOffset_sec = 0;

WebServer server(80);

void setup() {
  Serial.begin(9600);

  // Kết nối WiFi
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.print(".");
  }
  Serial.println("Đã kết nối WiFi!");
  Serial.print(WiFi.localIP());

  // Khởi tạo thời gian
  configTime(gmtOffset_sec, daylightOffset_sec, ntpServer);

  // Fetch initial values
  String defaultKey = getDefaultKey();
  String validCardId = getValidCardId();

  // Send initial values to Arduino
  Serial.println("CMD:UPDATE_KEY:" + defaultKey);
  delay(1000);
  Serial.println("CMD:UPDATE_CARD:" + validCardId);

  server.on("/control-door", HTTP_POST, handleDoorControl);
  server.begin();
  Serial.println("HTTP server started");
}

void loop() {
  server.handleClient();
  if (Serial.available()) {
    String command = Serial.readStringUntil('\n');
    command.trim();
    Serial.println("Received command: " + command);
    handleArduinoCommand(command);
  }
}

// void handleArduinoCommand(const String& command) {
//   if (command == "GET_DEFAULT_KEY") {
//     String defaultKey = getDefaultKey();
//     Serial.println("CMD:UPDATE_KEY:" + defaultKey);
//   } else if (command == "GET_VALID_CARD_ID") {
//     String validCardId = getValidCardId();
//     Serial.println("CMD:UPDATE_CARD:" + validCardId);
//   } else {
//     // Tách command thành loại truy cập và kết quả
//     int colonIndex = command.indexOf(':');
//     if (colonIndex != -1) {
//       String commandType = command.substring(0, colonIndex);
//       String accessResult = command.substring(colonIndex + 1);

//       Serial.println("Command Type: " + commandType);    // In ra để debug
//       Serial.println("Access Result: " + accessResult);  // In ra để debug

//       String accessType = "";
//       if (commandType == "LOG_KEYPAD") {
//         accessType = "KEYPAD";
//       } else if (commandType == "LOG_RFID") {
//         accessType = "RFID";
//       }

//       if (!accessType.isEmpty()) {
//         sendAccessLog(accessType, accessResult);
//       }
//     }
//   }
// }

void handleArduinoCommand(const String& command) {
  // if (command == "GET_DEFAULT_KEY") {
  //   String defaultKey = getDefaultKey();
  //   Serial.println("CMD:UPDATE_KEY:" + defaultKey);
  // } else if (command == "GET_VALID_CARD_ID") {
  //   String validCardId = getValidCardId();
  //   Serial.println("CMD:UPDATE_CARD:" + validCardId);
  // } else if (command == "OPEN_DOOR") {
  //   handleDoorControl(true);
  // } else if (command == "CLOSE_DOOR") {
  //   handleDoorControl(false);
  // } else if (command.startsWith("LOG_")) {
  //   // Tách command thành loại truy cập và kết quả
  //   int colonIndex = command.indexOf(':');
  //   if (colonIndex != -1) {
  //     String commandType = command.substring(0, colonIndex);
  //     String accessResult = command.substring(colonIndex + 1);

  //     Serial.println("Command Type: " + commandType);    // In ra để debug
  //     Serial.println("Access Result: " + accessResult);  // In ra để debug

  //     String accessType = "";
  //     if (commandType == "LOG_KEYPAD") {
  //       accessType = "KEYPAD";
  //     } else if (commandType == "LOG_RFID") {
  //       accessType = "RFID";
  //     }

  //     if (!accessType.isEmpty()) {
  //       sendAccessLog(accessType, accessResult);
  //     }
  //   }
  // }


  if (command == "GET_DEFAULT_KEY") {
    String defaultKey = getDefaultKey();
    Serial.println("CMD:UPDATE_KEY:" + defaultKey);
  } else if (command == "GET_VALID_CARD_ID") {
    String validCardId = getValidCardId();
    Serial.println("CMD:UPDATE_CARD:" + validCardId);
  } else if (command.startsWith("LOG_")) {
    // Tách command thành loại truy cập và kết quả
    int colonIndex = command.indexOf(':');
    if (colonIndex != -1) {
      String commandType = command.substring(0, colonIndex);
      String accessResult = command.substring(colonIndex + 1);

      Serial.println("Command Type: " + commandType);    // In ra để debug
      Serial.println("Access Result: " + accessResult);  // In ra để debug

      String accessType = "";
      if (commandType == "LOG_KEYPAD") {
        accessType = "KEYPAD";
      } else if (commandType == "LOG_RFID") {
        accessType = "RFID";
      }

      if (!accessType.isEmpty()) {
        sendAccessLog(accessType, accessResult);
      }
    }
  }
}


String getCurrentTime() {
  struct tm timeinfo;
  if (!getLocalTime(&timeinfo)) {
    return "";
  }
  char timeString[25];
  strftime(timeString, 25, "%Y-%m-%dT%H:%M:%S.000Z", &timeinfo);
  return String(timeString);
}

void sendAccessLog(const String& accessType, const String& accessResult) {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;

    StaticJsonDocument<200> doc;
    doc["accessType"] = accessType;
    doc["accessResult"] = accessResult;
    doc["accessTime"] = getCurrentTime();

    String jsonString;
    serializeJson(doc, jsonString);
    Serial.println("Sending JSON: " + jsonString);  // In ra để debug

    http.begin(client, API_URL);
    http.addHeader("Content-Type", "application/json");

    int httpResponseCode = http.POST(jsonString);

    if (httpResponseCode > 0) {
      String response = http.getString();
      // Serial.println(response);
      Serial.println("CMD:SUCCESS");
    } else {
      Serial.println("CMD:FAILED");
    }

    http.end();
  }
}

String getDefaultKey() {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;

    http.begin(DEFAULT_KEY_URL);
    int httpResponseCode = http.GET();

    if (httpResponseCode > 0) {
      String payload = http.getString();
      DynamicJsonDocument doc(1024);
      deserializeJson(doc, payload);
      return doc["keyValue"].as<String>();
    }

    http.end();
  }
  return "";
}

String getValidCardId() {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;

    http.begin(VALID_CARD_ID_URL);
    int httpResponseCode = http.GET();

    if (httpResponseCode > 0) {
      String payload = http.getString();
      DynamicJsonDocument doc(1024);
      deserializeJson(doc, payload);
      return doc["cardId"].as<String>();
    }

    http.end();
  }
  return "";
}

// void handleDoorControl(bool isOpen) {
//   if (WiFi.status() == WL_CONNECTED) {
//     HTTPClient http;
//     WiFiClient client;

//     StaticJsonDocument<200> doc;
//     doc["isOpen"] = isOpen;

//     String jsonString;
//     serializeJson(doc, jsonString);
//     Serial.println("Sending JSON: " + jsonString);  // In ra để debug

//     http.begin(client, DOOR_CONTROL_URL);
//     http.addHeader("Content-Type", "application/json");

//     int httpResponseCode = http.POST(jsonString);

//     if (httpResponseCode > 0) {
//       String response = http.getString();
//       Serial.println("CMD:" + String(isOpen ? "OPEN_DOOR" : "CLOSE_DOOR"));
//     } else {
//       Serial.println("CMD:FAILED");
//     }

//     http.end();
//   }
// }

void handleDoorControl() {
  String message;
  if (server.hasArg("plain")) {
    message = server.arg("plain");
    DynamicJsonDocument doc(1024);
    deserializeJson(doc, message);

    bool isOpen = doc["isOpen"];

    if (isOpen) {
      Serial.println("CMD:OPEN_DOOR");
      server.send(200, "application/json", "{\"success\":true,\"message\":\"Door opened\"}");
    } else {
      Serial.println("CMD:CLOSE_DOOR");
      server.send(200, "application/json", "{\"success\":true,\"message\":\"Door closed\"}");
    }
  } else {
    server.send(400, "application/json", "{\"success\":false,\"error\":\"No data received\"}");
  }
}
