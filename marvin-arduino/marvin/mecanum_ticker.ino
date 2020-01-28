/*
 * Checks the current from the motors of the mecanum drive and stops, if the current is greater than 2Amps
 */
void checkCurrent()
{
    if (mecanumDrive.getCurrent(FRONT_LEFT) > 2000) {
          mecanumDrive.setSpeed(FRONT_LEFT, 0);  
    }
    if (mecanumDrive.getCurrent(REAR_LEFT) > 2000) {
        mecanumDrive.setSpeed(REAR_LEFT, 0);  
    }
    if (mecanumDrive.getCurrent(FRONT_RIGHT) > 2000) {
        mecanumDrive.setSpeed(FRONT_RIGHT, 0);  
    }
    if (mecanumDrive.getCurrent(REAR_RIGHT) > 2000) {
        mecanumDrive.setSpeed(REAR_RIGHT, 0);  
    }
}



/*
 * Reads the current from the mecanum drive and sends the data to the Pi
 */

void sendCurrent() {
    sendCurrent("");
}

void sendCurrent(const String& messageUid) {

    String message = "MEC_CURR,,";

    message += messageUid;
    message += ",";
    message += mecanumDrive.getCurrent(FRONT_LEFT);
    message += ",";
    message += mecanumDrive.getCurrent(REAR_LEFT);
    message += ",";
    message += mecanumDrive.getCurrent(FRONT_RIGHT);
    message += ",";
    message += mecanumDrive.getCurrent(REAR_RIGHT);

    message = "$" + message + "*" + checkSum(message);
    Serial2.println(message);
    delay(100);
}

/*
 * Reads the motor information (direction,speed) from the mecanum drive and sends the data to the Pi
 */
void sendMotorInfo() {
    sendMotorInfo("");
} 
void sendMotorInfo(const String& messageUid) {

    String message = "MEC_INFO,,";

    message += messageUid;
    message += ",";
    message += mecanumDrive.getMotorDirection(FRONT_LEFT);
    message += ",";
    message += mecanumDrive.getSpeed(FRONT_LEFT);
    message += ",";
    message += mecanumDrive.getMotorDirection(REAR_LEFT);
    message += ",";
    message += mecanumDrive.getSpeed(REAR_LEFT);
    message += ",";
    message += mecanumDrive.getMotorDirection(FRONT_RIGHT);
    message += ",";
    message += mecanumDrive.getSpeed(FRONT_RIGHT);
    message += ",";
    message += mecanumDrive.getMotorDirection(REAR_RIGHT);
    message += ",";
    message += mecanumDrive.getSpeed(REAR_RIGHT);    

    message = "$" + message + "*" + checkSum(message);
    Serial2.println(message);
    delay(100);
}

/*
 * check, if we have to adjust the PWM signal to get good values for the different motors
 */
void correctSpeed()
{
  mecanumDrive.correctSpeed();
}
