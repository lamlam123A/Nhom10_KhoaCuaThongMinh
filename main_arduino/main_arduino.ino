#include <SPI.h>
#include <MFRC522.h>
#include <Keypad.h>
#include <Wire.h>
#include <LiquidCrystal_I2C.h>
#include <Servo.h>

// Khởi tạo các đối tượng
LiquidCrystal_I2C lcd(0x27, 16, 2);
MFRC522 mfrc522(10, 9);  // SS_PIN: 10, RST_PIN: 9
Servo doorServo;

// Định nghĩa các chân
const int LED_GREEN = 7;
const int LED_RED = 6;
const int BUZZER = 8;
const int SERVO_PIN = 2;


// Các biến cấu hình
const int SERVO_OPEN = 90;
const int SERVO_CLOSED = 0;
String keypadCode = "1357";       // Mã mặc định, sẽ được cập nhật
String rfidCode = "23 35 5A ED";  // Mã RFID mặc định, sẽ được cập nhật
int failCount = 0;


// Cấu hình bàn phím
const byte ROWS = 5;
const byte COLS = 3;
char keys[ROWS][COLS] = {
  { 'M', 'C', '#' },
  { '1', '2', '3' },
  { '4', '5', '6' },
  { '7', '8', '9' },
  { 'L', '0', 'R' }
}; // M: Mode | C: Close

byte rowPins[ROWS] = { A4, A3, A2, A1, A0 };
byte colPins[COLS] = { 5, 4, 3 };
Keypad keypad = Keypad(makeKeymap(keys), rowPins, colPins, ROWS, COLS);

// Biến trạng thái
bool isKeypadMode = true;
bool isDoorOpen = false;
String enteredCode = "";
String hiddenCode = "";
volatile bool isDoorInitialized = false;

// Biến thời gian
unsigned long lastUpdateTime = 0;
const unsigned long UPDATE_INTERVAL = 300000;  // 5 phút

// Các định nghĩa mới cho mẫu âm thanh
#define BEEP_SHORT 100
#define BEEP_LONG 300
#define BEEP_PAUSE 100

unsigned long lockStartTime = 0;
const unsigned long LOCK_DURATION = 600000;  // 10 phút (600,000 milliseconds)
bool isLocked = false;                       // Biến để kiểm tra nếu hệ thống đang bị khóa


// Khai báo hàm trước
void closeDoor(bool isInitializing = false);
void openDoor();
void updateLCDHeader();


void setup() {
  initializeHardware();
  requestInitialValues();
}

void loop() {
  updateLCDHeader();

  if (isLocked) {
    checkLockStatus();  // Kiểm tra trạng thái khóa
  } else {
    if (isKeypadMode) {
      handleKeypadInput();
    } else {
      handleRFIDInput();
    }

    checkForESPUpdates();
    requestPeriodicUpdates();
  }

  delay(50);
}


// Khởi tạo phần cứng
void initializeHardware() {
  lcd.init();
  lcd.backlight();
  lcd.clear();

  doorServo.attach(SERVO_PIN);

  Serial.begin(9600);
  SPI.begin();
  mfrc522.PCD_Init();

  pinMode(LED_GREEN, OUTPUT);
  pinMode(LED_RED, OUTPUT);
  pinMode(BUZZER, OUTPUT);

  // Khởi tạo cửa ở trạng thái đóng
  closeDoor(true);
}

// Yêu cầu giá trị ban đầu từ ESP8266
void requestInitialValues() {
  Serial.println("GET_DEFAULT_KEY");
  Serial.println("GET_VALID_CARD_ID");
}

// Cập nhật tiêu đề LCD
void updateLCDHeader() {
  lcd.setCursor(0, 0);
  lcd.print(isKeypadMode ? "=>>> Nhap Ma" : "=>>> Quet The");
}

// Xử lý đầu vào bàn phím
void handleKeypadInput() {
  char key = keypad.getKey();
  if (key) {
    if (key == 'M') {
      switchToRFIDMode();
    } else if (key == 'C' && isDoorInitialized) {
      closeDoor(false);
    } else {
      processKeypadEntry(key);
    }
  }
}

// Xử lý đầu vào RFID
void handleRFIDInput() {
  if (mfrc522.PICC_IsNewCardPresent() && mfrc522.PICC_ReadCardSerial()) {
    String uid = getRFIDUID();
    authenticateAccess(uid, false);
  } else {
    char key = keypad.getKey();
    if (key == 'M') {
      switchToKeypadMode();
    } else if (key == 'C' && isDoorInitialized) {
      closeDoor(false);
    }
  }
}

// Chuyển sang chế độ RFID
void switchToRFIDMode() {
  isKeypadMode = false;
  lcd.clear();
  resetEnteredCode();
}

// Chuyển sang chế độ bàn phím
void switchToKeypadMode() {
  isKeypadMode = true;
  lcd.clear();
  resetEnteredCode();
}

// Xử lý nhập mã từ bàn phím
void processKeypadEntry(char key) {
  enteredCode += key;
  hiddenCode += "*";
  updateLCDWithCode();

  if (enteredCode.length() == 4) {
    authenticateAccess(enteredCode, true);
    resetEnteredCode();
  }
}

// Cập nhật LCD với mã đã nhập
void updateLCDWithCode() {
  lcd.setCursor(0, 1);
  lcd.print(hiddenCode + "                ");  // Xóa các ký tự thừa
}

// Đặt lại mã đã nhập
void resetEnteredCode() {
  enteredCode = "";
  hiddenCode = "";
  lcd.setCursor(0, 1);
  lcd.print("                ");
}

// Xác thực quyền truy cập
void authenticateAccess(String code, bool isKeypadAuth) {
  lcd.setCursor(0, 1);
  bool isAuthorized = (isKeypadAuth && code == keypadCode) || (!isKeypadAuth && code == rfidCode);

  if (isAuthorized) {
    lcd.print("Truy cap OK    ");
    openDoor();
    logAccess(isKeypadAuth, true);
    failCount = 0;
  } else {
    failCount++;
    // lcd.print("Truy cap BI TU ");
    lcd.print("BLOCK!");
    // indicateFailedAccess();
    logAccess(isKeypadAuth, false);
    handleFailedAccess(failCount);
  }

  delay(2000);  // Hiển thị kết quả trong 2 giây
  lcd.clear();
  updateLCDHeader();
}

// Mở cửa
void openDoor() {
  if (!isDoorOpen && isDoorInitialized) {
    digitalWrite(LED_GREEN, HIGH);
    digitalWrite(LED_RED, LOW);
    doorServo.write(SERVO_OPEN);
    isDoorOpen = true;
  }
}

// Đóng cửa
void closeDoor(bool isInitializing) {
  doorServo.write(SERVO_CLOSED);
  digitalWrite(LED_GREEN, LOW);
  digitalWrite(LED_RED, HIGH);  // Bật đèn LED đỏ khi cửa đóng
  isDoorOpen = false;

  if (!isInitializing) {
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("Cua da dong");
    delay(1000);
    lcd.clear();
    updateLCDHeader();
  }

  digitalWrite(LED_RED, LOW);
  isDoorInitialized = true;
}

// Báo hiệu truy cập thất bại
void indicateFailedAccess(int numBeeps = 3, int flashDuration = 200) {
  for (int i = 0; i < numBeeps; i++) {
    // Bật đèn LED đỏ và còi báo
    digitalWrite(LED_RED, HIGH);
    digitalWrite(BUZZER, HIGH);

    // Giữ trong một khoảng thời gian
    delay(flashDuration);

    // Tắt đèn LED đỏ và còi báo
    digitalWrite(LED_RED, LOW);
    digitalWrite(BUZZER, LOW);

    // Tạm dừng giữa các tiếng bíp (trừ lần cuối cùng)
    if (i < numBeeps - 1) {
      delay(BEEP_PAUSE);
    }
  }

  // Thêm một khoảng lặng cuối cùng
  delay(BEEP_PAUSE);
}

// Hàm phát ra mẫu âm thanh SOS
void playSOSPattern() {
  // Mẫu SOS: ... --- ...
  for (int i = 0; i < 3; i++) {
    // Dấu chấm (.)
    for (int j = 0; j < 3; j++) {
      digitalWrite(BUZZER, HIGH);
      digitalWrite(LED_RED, HIGH);
      delay(BEEP_SHORT);
      digitalWrite(BUZZER, LOW);
      digitalWrite(LED_RED, LOW);
      delay(BEEP_PAUSE);
    }
    delay(BEEP_PAUSE);

    // Dấu gạch (-)
    for (int j = 0; j < 3; j++) {
      digitalWrite(BUZZER, HIGH);
      digitalWrite(LED_RED, HIGH);
      delay(BEEP_LONG);
      digitalWrite(BUZZER, LOW);
      digitalWrite(LED_RED, LOW);
      delay(BEEP_PAUSE);
    }
    delay(BEEP_PAUSE);
  }
}

// Hàm phát ra mẫu âm thanh cảnh báo
void playAlertPattern(int duration = 3000) {
  unsigned long startTime = millis();
  while (millis() - startTime < duration) {
    digitalWrite(BUZZER, HIGH);
    digitalWrite(LED_RED, HIGH);
    delay(50);
    digitalWrite(BUZZER, LOW);
    digitalWrite(LED_RED, LOW);
    delay(50);
  }
}

// Hàm chính để báo hiệu truy cập thất bại
void handleFailedAccess(int failCount) {
  lcd.clear();
  lcd.setCursor(0, 0);
  // lcd.print("Truy cap BI TU");
  lcd.print("BLOCK!");

  if (failCount == 1) {
    indicateFailedAccess(2, 200);  // 2 tiếng bíp ngắn
  } else if (failCount == 2) {
    indicateFailedAccess(3, 300);  // 3 tiếng bíp dài hơn
  } else if (failCount == 3) {
    lcd.setCursor(0, 1);
    lcd.print("Canh bao!");
    playSOSPattern();
  } else {
    lcd.setCursor(0, 1);
    lcd.print("HE THONG KHOA!");
    playAlertPattern(5000);    // Cảnh báo kéo dài 5 giây
    isLocked = true;           // Đặt cờ khóa
    lockStartTime = millis();  // Lưu thời gian bắt đầu khóa
  }

  delay(1000);  // Hiển thị thông báo thêm 1 giây
  lcd.clear();
  updateLCDHeader();
}

// Lấy UID của thẻ RFID
String getRFIDUID() {
  String uid = "";
  for (byte i = 0; i < mfrc522.uid.size; i++) {
    uid += (mfrc522.uid.uidByte[i] < 0x10 ? "0" : "") + String(mfrc522.uid.uidByte[i], HEX) + (i != mfrc522.uid.size - 1 ? " " : "");
  }
  uid.toUpperCase();
  return uid;
}

// Ghi log truy cập
void logAccess(bool isKeypadAuth, bool isGranted) {
  Serial.println(String("LOG_") + (isKeypadAuth ? "KEYPAD:" : "RFID:") + (isGranted ? "GRANTED" : "DENIED"));
  // String accessType = isKeypadAuth ? "KEYPAD" : "RFID";
  // String accessResult = isGranted ? "GRANTED" : "DENIED";
  // Serial.println("LOG_" + accessType + ":" + accessResult);
}

// Kiểm tra cập nhật từ ESP8266
void checkForESPUpdates() {
  if (Serial.available()) {
    String response = Serial.readStringUntil('\n');
    response.trim();
    // Serial.println("Received command: " + response);
    handleESPResponse(response);
  }
}

// Xử lý phản hồi từ ESP8266
void handleESPResponse(String response) {
  if (response.startsWith("CMD:UPDATE_KEY:")) {
    keypadCode = response.substring(15);
    keypadCode.trim();
    Serial.println("Key updated: " + keypadCode);
  } else if (response.startsWith("CMD:UPDATE_CARD:")) {
    rfidCode = response.substring(16);
    rfidCode.trim();
    Serial.println("Card updated: " + rfidCode);
  } else if (response == "CMD:OPEN_DOOR") {
    openDoor();
  } else if (response == "CMD:CLOSE_DOOR") {
    closeDoor();
  }
}

// Yêu cầu cập nhật định kỳ
void requestPeriodicUpdates() {
  if (millis() - lastUpdateTime > UPDATE_INTERVAL) {
    Serial.println("GET_DEFAULT_KEY");
    Serial.println("GET_VALID_CARD_ID");
    lastUpdateTime = millis();
  }
}

// Kiểm tra trạng thái khóa và hiển thị thời gian đếm ngược:
void checkLockStatus() {
  if (isLocked) {
    unsigned long elapsedTime = millis() - lockStartTime;
    unsigned long remainingTime = LOCK_DURATION - elapsedTime;

    if (remainingTime > 0) {
      // Tính toán số phút và giây còn lại
      unsigned long minutesLeft = remainingTime / 60000;
      unsigned long secondsLeft = (remainingTime % 60000) / 1000;

      // Hiển thị thời gian đếm ngược trên LCD
      lcd.setCursor(0, 1);
      lcd.print("Khoa: ");
      lcd.print(minutesLeft);
      lcd.print("p ");
      lcd.print(secondsLeft);
      lcd.print("s ");
    } else {
      // Hết thời gian khóa, mở khóa hệ thống
      isLocked = false;
      failCount = 0;  // Đặt lại failCount
      lcd.clear();
      updateLCDHeader();  // Cập nhật lại màn hình
    }
  }
}
