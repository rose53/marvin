#include <Ticker.h>
#include <Wire.h>
#include <Dagu4Motor.h>
#include <MecanumDrive.h>

#define DIRECTION_FL 28
#define DIRECTION_RL 26
#define DIRECTION_FR 24
#define DIRECTION_RR 22

#define PWM_MOTOR_FL 7
#define PWM_MOTOR_RL 6
#define PWM_MOTOR_FR 5
#define PWM_MOTOR_RR 8

// the following are for external interrupts (connected to the encoder pins)
#define ENC_MOTOR_FL 0    // equals pin 2
#define ENC_MOTOR_RL 1    // equals pin 3
#define ENC_MOTOR_FR 4    // equals pin 19
#define ENC_MOTOR_RR 5    // equals pin 18

#define TRIGGER_PIN  52  // Arduino pin tied to trigger pin on the ultrasonic sensor.
#define ECHO_PIN     53  // Arduino pin tied to echo pin on the ultrasonic sensor.
#define MAX_DISTANCE 400 // Maximum distance we want to ping for (in centimeters). Maximum sensor distance is rated at 400-500cm.

#define COMMAND_READ_MECANUM_MOTOR_INFO 65
#define COMMAND_READ_DISTANCE 66

const int dirPins[4]  = {DIRECTION_FL, DIRECTION_RL , DIRECTION_FR, DIRECTION_RR}; // direction pins
const int pwmPins[4]  = {PWM_MOTOR_FL, PWM_MOTOR_RL, PWM_MOTOR_FR, PWM_MOTOR_RR};  // pwm pins
const int currPins[4] = {A3, A2, A1, A0};                                          // current pins
const int encPins[4]  = {ENC_MOTOR_FL, ENC_MOTOR_RL, ENC_MOTOR_FR, ENC_MOTOR_RR};  // encoder pins


void checkCurrent();
void sendCurrent();
void correctSpeed();

MecanumDrive mecanumDrive;


int address = 0;

// the data buffer holds the data send from the pi, the index points to the next free position to write the recieved data
char dataBuffer[1024];
int dataBufferIndex = 0;

volatile unsigned int distance;


Ticker checkCurrentTicker(checkCurrent,1000); // checks, if the current of a motor is above 2Amps every 1000 ms
Ticker sendCurrentTicker(sendCurrent,30000); // sends the actual current consumption every 30sec
Ticker correctSpeedTicker(correctSpeed,50); // checks, if the current of a motor is above 2Amps every 1000 ms

void setup() {

    Serial.begin(9600);  // start serial for output
    Serial2.begin(57600); // pi

    Serial.println("Ready!");
    mecanumDrive.open(pwmPins, dirPins, currPins, encPins);
    //mecanumDrive.setControlSpeedStrategy(&(ControlSpeedStrategy::exponentialStrategy));
    
    pinMode(ECHO_PIN, INPUT);
    pinMode(TRIGGER_PIN, OUTPUT);

    // start the ticker objects
    checkCurrentTicker.start();
    sendCurrentTicker.start();
    correctSpeedTicker.start();
}

void loop() {  
    
    while (Serial2.available()) {
        dataBuffer[dataBufferIndex++] = Serial2.read(); //read pi data
        // read data until we get a \n, a \n marks the end of one message from the pi
        if (dataBuffer[dataBufferIndex - 1] == '\n') {
            dataBuffer[dataBufferIndex - 1 ] = '\0';            
            handleMessage(String(dataBuffer));
            dataBufferIndex = 0;
        }
    }
  
    // update the ticker objects
    correctSpeedTicker.update();
    sendCurrentTicker.update();
    checkCurrentTicker.update();
  
  /*
  digitalWrite(TRIGGER_PIN, LOW);                   // Set the trigger pin to low for 2uS
  delayMicroseconds(2);
  digitalWrite(TRIGGER_PIN, HIGH);                  // Send a 10uS high to trigger ranging
  delayMicroseconds(10);
  digitalWrite(TRIGGER_PIN, LOW);                   // Send pin low again
  distance = pulseIn(ECHO_PIN, HIGH);        // Read in times pulse
  distance= distance/58;                        // Calculate distance from time of pulse
  */
}

// calculate the checksum:
String checkSum(const String& data) {
    char check = 0;
    // iterate over the string, XOR each byte with the total sum:
    for (int c = 0; c < data.length(); c++) {
        check = char(check ^ data.charAt(c));
    }
    // return the result
    String hexString = String(check, HEX);
    if (hexString.length() == 1) {
        hexString = "0" + hexString;
    }
    return hexString;
}

bool isCheckSumValid(const String& data, const String& checksum) {
  return checkSum(data) == checksum;
}

/**
 * This method handles the message recieved from the pi
 */
void handleMessage(const String& message) {
    Serial.println("handleMessage: recieved the following message: " + message);
    // the checksum is located after the '*' in the message
    int indexOfAsterix = message.lastIndexOf('*');
    String dataString = message.substring(1, indexOfAsterix);
    if (!isCheckSumValid(dataString, message.substring(indexOfAsterix + 1))) {
        Serial.println("handleMessage: checksum failed");
        return;
    }
  
    int messageIdPos = dataString.indexOf(',');
    String messageType = dataString.substring(0, messageIdPos);
    int messageUidPos = dataString.indexOf(',', messageIdPos + 1);
    String messageId = dataString.substring(messageIdPos + 1, messageUidPos);
    int dataPos = dataString.indexOf(',', messageUidPos + 1);
    String messageUid = dataString.substring(messageUidPos + 1, dataPos);
    if (messageType == "MEC") {
        // we have a message for the mecanum drive, get the three values 
        int tmpDataPos = dataPos;
        dataPos = dataString.indexOf(',', dataPos + 1);
        int ch1 = dataString.substring(tmpDataPos + 1, dataPos).toInt();
        tmpDataPos = dataPos;
        dataPos = dataString.indexOf(',', dataPos + 1);
        int ch3 = dataString.substring(tmpDataPos + 1, dataPos).toInt();
        tmpDataPos = dataPos;
        dataPos = dataString.indexOf(',', dataPos + 1);
        int ch4 = dataString.substring(tmpDataPos + 1, dataPos).toInt();
        mecanumDrive.setValue(ch1, ch3, ch4);
    }
}


// callback for sending data
void sendData() {
  //  Serial.print("data requested: ");
  if (address == COMMAND_READ_MECANUM_MOTOR_INFO) {
    readCommand65();
  } else if (address == COMMAND_READ_DISTANCE) {
    readCommand66();
  }
}
