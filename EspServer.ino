#include <WiFi.h>

const char* ssid = "subway";              
const char* password = "44444444";       

WiFiServer server(3333);

void setup() {
  Serial.begin(115200);  // USB 시리얼 출력
  Serial2.begin(9600, SERIAL_8N1, 16, 17);  // UART2: TX=17 → Uno RX

  WiFi.softAP(ssid, password);
  IPAddress IP = WiFi.softAPIP();
  Serial.print("SoftAP IP address: ");
  Serial.println(IP);

  server.begin();
  Serial.println("Server started, waiting for clients...");
}

void loop() {
  WiFiClient client = server.available();
  if (client) {
    Serial.println("Client connected");

    String fullMessage = "";
    unsigned long lastReceive = millis();

    while (client.connected()) {
      while (client.available()) {
        char c = client.read();
        fullMessage += c;
        lastReceive = millis();

        if (c == '\n') {
          Serial.print("받은 메시지: ");
          Serial.println(fullMessage);
          processMessage(fullMessage);
          fullMessage = "";
        }
      }

      if (millis() - lastReceive > 30000) {
        Serial.println("Client timeout, disconnecting.");
        break;
      }
    }

    client.stop();
    Serial.println("Client disconnected");
  }
}

void processMessage(String message) {
  int idIndex = message.indexOf("id=");
  int countIndex = message.indexOf("count=");

  if (idIndex != -1 && countIndex != -1) {
    int idValue = message.substring(idIndex + 3, message.indexOf(',', idIndex)).toInt();
    int countValue = message.substring(countIndex + 6).toInt();

    Serial.print("ID: ");
    Serial.print(idValue);
    Serial.print(" /BLE): ");
    Serial.println(countValue);

    // ✅ 아두이노 Uno에 전송할 문자열 생성 및 전송
    String sendMessage = String(idValue) + "," + String(countValue) + "\n";
    Serial2.print(sendMessage);  // UART2 → Uno로 전송

    // 디버깅용
    Serial.print("📤 Uno로 전송: ");
    Serial.print(sendMessage);

  } else {
    Serial.println("⚠️ 메시지 형식 오류");
  }
}
