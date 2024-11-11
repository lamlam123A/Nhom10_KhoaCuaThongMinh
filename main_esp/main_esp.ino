#include <WiFi.h>
#include <WebServer.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>
#include "esp_camera.h"
#include "esp_http_server.h"
#include "time.h"

// Camera pins for AI Thinker ESP32-CAM
#define PWDN_GPIO_NUM 32
#define RESET_GPIO_NUM -1
#define XCLK_GPIO_NUM 0
#define SIOD_GPIO_NUM 26
#define SIOC_GPIO_NUM 27
#define Y9_GPIO_NUM 35
#define Y8_GPIO_NUM 34
#define Y7_GPIO_NUM 39
#define Y6_GPIO_NUM 36
#define Y5_GPIO_NUM 21
#define Y4_GPIO_NUM 19
#define Y3_GPIO_NUM 18
#define Y2_GPIO_NUM 5
#define VSYNC_GPIO_NUM 25
#define HREF_GPIO_NUM 23
#define PCLK_GPIO_NUM 22

// WiFi configuration
const char* ssid = "TP-LINK_E308";
const char* password = "99990000";
// const char* ssid = "iPhonee";
// const char* password = "123123123";

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
  delay(3000);

  // Serial.println("\nStarting WiFi connection...");
  // Serial.printf("Connecting to SSID: %s\n", ssid);

  // WiFi.mode(WIFI_STA); // Set WiFi to station mode
  // WiFi.begin(ssid, password);

  // int attempt = 0;
  // while (WiFi.status() != WL_CONNECTED && attempt < 20) { // Timeout after 20 attempts
  //   delay(1000);
  //   Serial.printf("Attempt %d: WiFi status = %d\n", attempt + 1, WiFi.status());
  //   Serial.print(".");
  //   attempt++;
  // }

  // if (WiFi.status() == WL_CONNECTED) {
  //   Serial.println("\nWiFi Connected!");
  //   Serial.print("Local IP Address: ");
  //   Serial.println(WiFi.localIP());
  //   Serial.print("Signal Strength (RSSI): ");
  //   Serial.print(WiFi.RSSI());
  //   Serial.println(" dBm");
  // } else {
  //   Serial.println("\nWiFi Connection Failed!");
  //   Serial.println("Error Status: ");
  //   switch(WiFi.status()) {
  //     case WL_IDLE_STATUS: Serial.println("Idle"); break;
  //     case WL_NO_SSID_AVAIL: Serial.println("SSID not available"); break;
  //     case WL_CONNECT_FAILED: Serial.println("Connection Failed"); break;
  //     case WL_CONNECTION_LOST: Serial.println("Connection Lost"); break;
  //     case WL_DISCONNECTED: Serial.println("Disconnected"); break;
  //     default: Serial.printf("Unknown Status: %d\n", WiFi.status());
  //   }
  // }


  // Connect to WiFi
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.print(".");
  }
  Serial.println("Connected to WiFi!");
  Serial.println(WiFi.localIP());

  // Initialize camera
  setupCamera();

  // Initialize time
  configTime(gmtOffset_sec, daylightOffset_sec, ntpServer);

  // Setup web server routes
  server.on("/", HTTP_GET, handleRoot);
  server.on("/stream", HTTP_GET, handleStream);
  server.on("/control-door", HTTP_POST, handleDoorControl);

  server.begin();
  Serial.println("HTTP server started");

  // Fetch and send initial values
  String defaultKey = getDefaultKey();
  String validCardId = getValidCardId();
  Serial.println("CMD:UPDATE_KEY:" + defaultKey);
  delay(1000);
  Serial.println("CMD:UPDATE_CARD:" + validCardId);
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

void handleArduinoCommand(const String& command) {
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
      // String commandType = command.substring(0, colonIndex);
      // String accessResult = command.substring(colonIndex + 1);

      // Serial.println("Command Type: " + commandType);    // In ra để debug
      // Serial.println("Access Result: " + accessResult);  // In ra để debug

      String commandType = command.substring(0, colonIndex);
      int secondColonIndex = command.indexOf(':', colonIndex + 1);
      if (secondColonIndex != -1) {
        String accessResult = command.substring(colonIndex + 1, secondColonIndex);
        String doorStatus = command.substring(secondColonIndex + 1);

        Serial.println("Command Type: " + commandType);
        Serial.println("Access Result: " + accessResult);
        Serial.println("Door Status: " + doorStatus);

        String accessType = "";
        if (commandType == "LOG_KEYPAD") {
          accessType = "KEYPAD";
        } else if (commandType == "LOG_RFID") {
          accessType = "RFID";
        }

        if (!accessType.isEmpty()) {
          sendAccessLog(accessType, accessResult, doorStatus);
        }
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

void sendAccessLog(const String& accessType, const String& accessResult, const String& doorStatus) {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;

    StaticJsonDocument<200> doc;
    doc["accessType"] = accessType;
    doc["doorStatus"] = doorStatus;
    doc["accessResult"] = accessResult;
    doc["accessTime"] = getCurrentTime();

    String jsonString;
    serializeJson(doc, jsonString);
    Serial.println("Sending JSON: " + jsonString);

    http.begin(API_URL);
    http.addHeader("Content-Type", "application/json");

    int httpResponseCode = http.POST(jsonString);

    if (httpResponseCode > 0) {
      String response = http.getString();
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



void handleDoorControl() {
  String message;
  if (server.hasArg("plain")) {
    message = server.arg("plain");
    DynamicJsonDocument doc(1024);
    deserializeJson(doc, message);

    bool isOpen = doc["isOpen"];

    if (isOpen) {
      Serial.println("CMD:OPEN_DOOR");
      sendAccessLog("APP", "GRANTED", "OPEN");
      server.send(200, "application/json", "{\"success\":true,\"message\":\"Door opened\"}");
    } else {
      Serial.println("CMD:CLOSE_DOOR");
      sendAccessLog("APP", "GRANTED", "CLOSED");
      server.send(200, "application/json", "{\"success\":true,\"message\":\"Door closed\"}");
    }
  } else {
    server.send(400, "application/json", "{\"success\":false,\"error\":\"No data received\"}");
  }
}


void setupCamera() {
  camera_config_t config;
  config.ledc_channel = LEDC_CHANNEL_0;
  config.ledc_timer = LEDC_TIMER_0;
  config.pin_d0 = Y2_GPIO_NUM;
  config.pin_d1 = Y3_GPIO_NUM;
  config.pin_d2 = Y4_GPIO_NUM;
  config.pin_d3 = Y5_GPIO_NUM;
  config.pin_d4 = Y6_GPIO_NUM;
  config.pin_d5 = Y7_GPIO_NUM;
  config.pin_d6 = Y8_GPIO_NUM;
  config.pin_d7 = Y9_GPIO_NUM;
  config.pin_xclk = XCLK_GPIO_NUM;
  config.pin_pclk = PCLK_GPIO_NUM;
  config.pin_vsync = VSYNC_GPIO_NUM;
  config.pin_href = HREF_GPIO_NUM;
  config.pin_sscb_sda = SIOD_GPIO_NUM;
  config.pin_sscb_scl = SIOC_GPIO_NUM;
  config.pin_pwdn = PWDN_GPIO_NUM;
  config.pin_reset = RESET_GPIO_NUM;
  config.xclk_freq_hz = 20000000;
  config.pixel_format = PIXFORMAT_JPEG;

  // Initial settings
  config.frame_size = FRAMESIZE_VGA;
  // config.jpeg_quality = 10;  // 0-63, lower means higher quality
  config.jpeg_quality = 30;  // 0-63, với 0 là chất lượng thấp nhất, 63 là cao nhất
  config.fb_count = 2;

  // Camera init
  esp_err_t err = esp_camera_init(&config);
  if (err != ESP_OK) {
    Serial.printf("Camera init failed with error 0x%x", err);
    return;
  }

  sensor_t* s = esp_camera_sensor_get();
  s->set_brightness(s, 0);                  // -2 to 2
  s->set_contrast(s, 0);                    // -2 to 2
  s->set_saturation(s, 0);                  // -2 to 2
  s->set_special_effect(s, 0);              // 0 = No Effect, 1 = Negative, 2 = Grayscale, 3 = Red Tint, 4 = Green Tint, 5 = Blue Tint, 6 = Sepia
  s->set_whitebal(s, 1);                    // 0 = disable , 1 = enable
  s->set_awb_gain(s, 1);                    // 0 = disable , 1 = enable
  s->set_wb_mode(s, 0);                     // 0 to 4 - if awb_gain enabled (0 - Auto, 1 - Sunny, 2 - Cloudy, 3 - Office, 4 - Home)
  s->set_exposure_ctrl(s, 1);               // 0 = disable , 1 = enable
  s->set_aec2(s, 0);                        // 0 = disable , 1 = enable
  s->set_gain_ctrl(s, 1);                   // 0 = disable , 1 = enable
  s->set_agc_gain(s, 0);                    // 0 to 30
  s->set_gainceiling(s, (gainceiling_t)0);  // 0 to 6
  s->set_bpc(s, 0);                         // 0 = disable , 1 = enable
  s->set_wpc(s, 1);                         // 0 = disable , 1 = enable
  s->set_raw_gma(s, 1);                     // 0 = disable , 1 = enable
  s->set_lenc(s, 1);                        // 0 = disable , 1 = enable
  s->set_hmirror(s, 0);                     // 0 = disable , 1 = enable
  s->set_vflip(s, 0);                       // 0 = disable , 1 = enable
  s->set_dcw(s, 1);                         // 0 = disable , 1 = enable
  s->set_colorbar(s, 0);                    // 0 = disable , 1 = enable
}

void handleRoot() {
  String html = "<html><head>";
  html += "<title>ESP32-CAM Video Stream</title>";
  html += "<meta name='viewport' content='width=device-width, initial-scale=1'>";
  html += "<style>";
  html += "body { font-family: Arial, sans-serif; text-align: center; margin: 0; padding: 20px; background-color: #f0f0f0; }";
  html += ".container { max-width: 800px; margin: 0 auto; }";
  html += ".video-container { position: relative; width: 100%; max-width: 640px; margin: 20px auto; }";
  html += "#stream { width: 100%; max-width: 640px; height: auto; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }";
  html += "</style>";
  html += "</head><body>";
  html += "<div class='container'>";
  html += "<h1>ESP32-CAM Video Stream</h1>";
  html += "<div class='video-container'>";
  html += "<img id='stream' src='/stream' />";
  html += "</div>";
  html += "</div>";
  html += "</body></html>";
  server.send(200, "text/html", html);
}

void handleStream() {
  WiFiClient client = server.client();

  String response = "HTTP/1.1 200 OK\r\n";
  response += "Content-Type: multipart/x-mixed-replace; boundary=frame\r\n\r\n";
  client.print(response);

  while (client.connected()) {
    camera_fb_t* fb = esp_camera_fb_get();
    if (!fb) {
      Serial.println("Camera capture failed");
      delay(1000);
      continue;
    }

    String header = "--frame\r\n";
    header += "Content-Type: image/jpeg\r\n";
    header += "Content-Length: " + String(fb->len) + "\r\n\r\n";
    client.print(header);
    client.write(fb->buf, fb->len);
    client.print("\r\n");

    esp_camera_fb_return(fb);
    delay(10);  // Slight delay to prevent overwhelming the client
  }
}