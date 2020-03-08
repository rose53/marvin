
void sendPanTilt() {
    sendPanTilt("");
}


void sendPanTilt(const String& messageUid) {
    
    String message = "PAN_TILT_INFO,,";

    message += messageUid;
    message += ",";
    message += servoPan.read() - PAN_ZERO ;
    message += ",";
    message += servoTilt.read() - TILT_ZERO ;
    message = "$" + message + "*" + checkSum(message);
    Serial2.println(message);    
}
