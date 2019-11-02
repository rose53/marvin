
#include <Wire.h>
#include <Dagu4Motor.h>
#include <MecanumDrive.h>
#include <Servo.h>

#define SLAVE_ADDRESS 0x04

#define DIRECTION_FL 28
#define DIRECTION_RL 26
#define DIRECTION_FR 24
#define DIRECTION_RR 22

#define PWM_MOTOR_FL 7
#define PWM_MOTOR_RL 6
#define PWM_MOTOR_FR 5
#define PWM_MOTOR_RR 8

#define ENC_MOTOR_FL 0
#define ENC_MOTOR_RL 1
#define ENC_MOTOR_FR 4
#define ENC_MOTOR_RR 5

#define TRIGGER_PIN  52  // Arduino pin tied to trigger pin on the ultrasonic sensor.
#define ECHO_PIN     53  // Arduino pin tied to echo pin on the ultrasonic sensor.
#define MAX_DISTANCE 400 // Maximum distance we want to ping for (in centimeters). Maximum sensor distance is rated at 400-500cm.

#define COMMAND_WRITE_CONTROL_MOTORS 3
#define COMMAND_WRITE_PAN_TILT 4
#define COMMAND_WRITE_PAN 5
#define COMMAND_WRITE_TILT 6
#define COMMAND_WRITE_INC_PAN 7
#define COMMAND_WRITE_DEC_PAN 8
#define COMMAND_WRITE_INC_TILT 9
#define COMMAND_WRITE_DEC_TILT 10


#define COMMAND_READ_CURRENT 64
#define COMMAND_READ_MECANUM_MOTOR_INFO 65
#define COMMAND_READ_DISTANCE 66

const int dirPins[4]  = {DIRECTION_FL, DIRECTION_RL , DIRECTION_FR, DIRECTION_RR}; // direction pins
const int pwmPins[4]  = {PWM_MOTOR_FL, PWM_MOTOR_RL, PWM_MOTOR_FR, PWM_MOTOR_RR};  // pwm pins
const int currPins[4] = {A3, A2, A1, A0};                                          // current pins
const int encPins[4]  = {ENC_MOTOR_FL, ENC_MOTOR_RL, ENC_MOTOR_FR, ENC_MOTOR_RR};  // encoder pins

MecanumDrive mecanumDrive;

const int ledSequence[11] = {0,0,0,0,15,100,15,0,0,0,0};
const int ledPwmPins[4] = {10,11,12,13};
int ledSequenceIndex = 0;
boolean ledSequenceDirection = true; // true counts up, fals counts down

Servo panServo;
Servo tiltServo;

int pan = 100;
int tilt = 150;

int address = 0;
int state = 0;

volatile unsigned int distance;

void setup() {

  Serial.begin(9600);         // start serial for output
  // initialize i2c as slave
  Wire.begin(SLAVE_ADDRESS);

  // define callbacks for i2c communication
  Wire.onReceive(receiveData);
  Wire.onRequest(sendData);

  Serial.println("Ready!");
  mecanumDrive.open(pwmPins, dirPins, currPins, encPins);
  //mecanumDrive.setControlSpeedStrategy(&(ControlSpeedStrategy::exponentialStrategy));
  
  panServo.attach(4);
  tiltServo.attach(9);
  
  panServo.write(pan); 
  tiltServo.write(tilt); 
  
  for (int i = 0; i < 4; i++) {
    pinMode(ledPwmPins[i], OUTPUT);
  }
  
  pinMode(ECHO_PIN, INPUT);
  pinMode(TRIGGER_PIN, OUTPUT);
}

void loop() {  
    
  delay(50);
  // check, if we have to adjust the PWM signal to get good values for the different motors
  mecanumDrive.correctSpeed();
  
  // checks, if the current of a motor is above 2Amps
  checkCurrent();
  //knightRider();  
  
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

// callback for received data
void receiveData(int byteCount) {
    Serial.print("data received: ");
    Serial.println(byteCount);

  address = Wire.read();
    Serial.print("address : ");
    Serial.println(address);
  byte controlData[byteCount - 1];
  char controlData2[byteCount - 1];

  if (address == 1) {
    for (int i = 0; i < byteCount - 1; i++) {
      controlData[i] = Wire.read();
            Serial.print("data command1 received: ");
            Serial.println(controlData[i]);
    }
    command1(controlData, byteCount - 1);
  } else if (address == 2) {
    for (int i = 0; i < byteCount - 1; i++) {
      controlData2[i] = Wire.read();
    }
    command2(controlData2, byteCount - 1);
  } else if (address == COMMAND_WRITE_CONTROL_MOTORS) {
    for (int i = 0; i < byteCount - 1; i++) {
      controlData2[i] = Wire.read();
    }
    if (byteCount - 1 == 3) {
      command3(controlData2[0], controlData2[1], controlData2[2]);
    }
  } else if (address == COMMAND_WRITE_PAN_TILT) {
    for (int i = 0; i < byteCount - 1; i++) {
      controlData2[i] = Wire.read();
    }
    if (byteCount - 1 == 3) {
      command3(controlData2[0], controlData2[1], controlData2[2]);
    }
  } else if (address == COMMAND_WRITE_INC_PAN) {    
       panServo.write(constrain(panServo.read() + Wire.read(), 0, 180));  
       pan = panServo.read();  
  } else if (address == COMMAND_WRITE_DEC_PAN) {
       panServo.write(constrain(panServo.read() - Wire.read(), 0, 180));    
       pan = panServo.read();
  } else if (address == COMMAND_WRITE_INC_TILT) {
       tiltServo.write(constrain(tiltServo.read() + Wire.read(), 0, 180));    
       tilt = tiltServo.read();
  } else if (address == COMMAND_WRITE_DEC_TILT) {
       tiltServo.write(constrain(tiltServo.read() - Wire.read(), 0, 180));    
       tilt = tiltServo.read();
  }
}

// callback for sending data
void sendData() {
  //  Serial.print("data requested: ");
  if (address == COMMAND_READ_CURRENT) {
    readCommand64();
  } else if (address == COMMAND_READ_MECANUM_MOTOR_INFO) {
    readCommand65();
  } else if (address == COMMAND_READ_DISTANCE) {
    readCommand66();
  }
}


void command1(byte* controlData, size_t controlDataLenght) {

  mecanumDrive.setMotorDirection(FRONT_LEFT, controlData[1] > 0);
  mecanumDrive.setSpeed(FRONT_LEFT, controlData[0]);
  mecanumDrive.setMotorDirection(REAR_LEFT, controlData[3] > 0);
  mecanumDrive.setSpeed(REAR_LEFT, controlData[2]);
  mecanumDrive.setMotorDirection(FRONT_RIGHT, controlData[5] > 0);
  mecanumDrive.setSpeed(FRONT_RIGHT, controlData[4]);
  mecanumDrive.setMotorDirection(REAR_RIGHT, controlData[7] > 0);
  mecanumDrive.setSpeed(REAR_RIGHT, controlData[6]);
}


void command2(char* controlData, size_t controlDataLenght) {
  mecanumDrive.setValue(FRONT_LEFT, controlData[0]);
  mecanumDrive.setValue(REAR_LEFT, controlData[1]);
  mecanumDrive.setValue(FRONT_RIGHT, controlData[2]);
  mecanumDrive.setValue(REAR_RIGHT, controlData[3]);

}

void command3(char ch1, char ch3, char ch4) {
  mecanumDrive.setValue(ch1, ch3, ch4);
}
