#include <Ticker.h>
#include <Wire.h>
#include <Dagu4Motor.h>
#include <MecanumDrive.h>
#include <NewPing.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_HMC5883_U.h>
#include <Servo.h>

#define DIRECTION_FL 25
#define DIRECTION_RL 24
#define DIRECTION_FR 23
#define DIRECTION_RR 22

#define PWM_MOTOR_FL 11 
#define PWM_MOTOR_RL 10
#define PWM_MOTOR_FR  9
#define PWM_MOTOR_RR  8

#define PWM_MAXBOTIX_0 5
#define PWM_MAXBOTIX_1 6
#define PWM_MAXBOTIX_2 7

#define PWM_SERVO_PAN 12
#define PWM_SERVO_TILT 13

#define PAN_ZERO 75
#define TILT_ZERO 45

#define CELL1 8
#define CELL2 9
#define CELL1_CORRECTION_FACTOR  4.18 / 4.24
#define CELL2_CORRECTION_FACTOR  4.21 / 4.7
#define LIPO_READINGS 5
#define V_REF 5.0

// the following are for external interrupts (connected to the encoder pins)
#define ENC_MOTOR_FL 0    // equals pin 2
#define ENC_MOTOR_RL 1    // equals pin 3
#define ENC_MOTOR_FR 4    // equals pin 19
#define ENC_MOTOR_RR 5    // equals pin 18

#define TRIGGER_PIN  26  // Arduino pin tied to trigger pin on the ultrasonic sensor.
#define ECHO_PIN     27  // Arduino pin tied to echo pin on the ultrasonic sensor.
#define MAX_DISTANCE 200 // Maximum distance we want to ping for (in centimeters). Maximum sensor distance is rated at 400-500cm.

const int dirPins[4]  = {DIRECTION_FL, DIRECTION_RL , DIRECTION_FR, DIRECTION_RR}; // direction pins
const int pwmPins[4]  = {PWM_MOTOR_FL, PWM_MOTOR_RL, PWM_MOTOR_FR, PWM_MOTOR_RR};  // pwm pins
const int currPins[4] = {A3, A2, A1, A0};                                          // current pins
const int encPins[4]  = {ENC_MOTOR_FL, ENC_MOTOR_RL, ENC_MOTOR_FR, ENC_MOTOR_RR};  // encoder pins


void checkCurrent();
void sendCurrent();
void sendMotorInfo();
void sendDistance();
void sendHeading();
void sendLipoStatus();

void correctSpeed();

MecanumDrive mecanumDrive;
NewPing sonar(TRIGGER_PIN, ECHO_PIN, MAX_DISTANCE);
Adafruit_HMC5883_Unified mag = Adafruit_HMC5883_Unified(12345);
Servo servoPan;
Servo servoTilt;


// the data buffer holds the data send from the pi, the index points to the next free position to write the recieved data
char dataBuffer[1024];
int dataBufferIndex = 0;

volatile boolean enableCommunication = false;


Ticker checkCurrentTicker(checkCurrent,1000); // checks, if the current of a motor is above 2Amps every 1000 ms
Ticker sendCurrentTicker(sendCurrent,30000); // sends the actual current consumption every 30sec
Ticker sendMotorInfoTicker(sendMotorInfo,30000); // sends the actual motor information every 30sec
Ticker correctSpeedTicker(correctSpeed,50); // checks, if the current of a motor is above 2Amps every 2000 ms
Ticker distanceTicker(sendDistance,2000); // sends the distances every 500 ms
Ticker headingTicker(sendHeading,2000); // sends the heading every 2000 ms
Ticker lipoStatusTicker(sendLipoStatus,60000); // sends the lipo status every 60000 ms

void setup() {

    Serial.begin(9600);  // start serial for output
    Serial2.begin(57600); // pi

    Serial.println("Ready!");
    mecanumDrive.open(pwmPins, dirPins, currPins, encPins);
    //mecanumDrive.setControlSpeedStrategy(&(ControlSpeedStrategy::exponentialStrategy));
    
    pinMode(ECHO_PIN, INPUT);
    pinMode(TRIGGER_PIN, OUTPUT);

    pinMode(PWM_MAXBOTIX_0, INPUT);
    pinMode(PWM_MAXBOTIX_1, INPUT);
    pinMode(PWM_MAXBOTIX_2, INPUT);

    mag.begin();

    servoPan.attach(PWM_SERVO_PAN);
    servoTilt.attach(PWM_SERVO_TILT);

    servoPan.write(PAN_ZERO);
    servoTilt.write(TILT_ZERO);
    
    // start the ticker objects
    checkCurrentTicker.start();
    sendCurrentTicker.start();
    sendMotorInfoTicker.start();
    correctSpeedTicker.start();
    distanceTicker.start();
    headingTicker.start();
    lipoStatusTicker.start();
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
    sendMotorInfoTicker.update();
    checkCurrentTicker.update();  
    distanceTicker.update();
    headingTicker.update();
    lipoStatusTicker.update();
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
        sendMotorInfo(messageUid.c_str());
    } else if (messageType == "GET_MEC_CURR") {
        sendCurrent(messageUid.c_str());
    } else if (messageType == "GET_MEC_INFO") {
        sendMotorInfo(messageUid.c_str());
    } else if (messageType == "GET_HDG") {
        sendHeading(messageUid.c_str());
    } else if (messageType == "GET_US") {
        sendDistance(messageUid.c_str(),messageId);
    } else if (messageType == "OPEN") {
        enableCommunication = true;
    } else if (messageType == "CLOSE") {
        enableCommunication = false;
    } else if (messageType == "PAN") {
        int tmpDataPos = dataPos;
        dataPos = dataString.indexOf(',', dataPos + 1);
        int angle = dataString.substring(tmpDataPos + 1, dataPos).toInt() + PAN_ZERO; 
        angle = constrain(angle,25,125);       
        servoPan.write(angle);
        sendPanTilt();
    } else if (messageType == "PAN_INC") {
        int tmpDataPos = dataPos;
        dataPos = dataString.indexOf(',', dataPos + 1);
        int angle = servoPan.read() + dataString.substring(tmpDataPos + 1, dataPos).toInt(); 
        angle = constrain(angle,25,125);       
        servoPan.write(angle);
        sendPanTilt();
    } else if (messageType == "TILT") {
        int tmpDataPos = dataPos;
        dataPos = dataString.indexOf(',', dataPos + 1);
        int angle = dataString.substring(tmpDataPos + 1, dataPos).toInt() + TILT_ZERO;
        angle = constrain(angle,15,80);       
        servoTilt.write(constrain(angle,15,80));
        sendPanTilt();
    } else if (messageType == "TILT_INC") {
        int tmpDataPos = dataPos;
        dataPos = dataString.indexOf(',', dataPos + 1);
        int angle = servoTilt.read() + dataString.substring(tmpDataPos + 1, dataPos).toInt();
        angle = constrain(angle,15,80);       
        servoTilt.write(angle);
        sendPanTilt();
    } else if (messageType == "GET_PAN_TILT_INFO") {
        sendPanTilt(messageUid.c_str());
    } 
}
