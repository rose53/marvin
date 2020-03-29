

float getVoltage(int adcPin) {
    int reading = 0;
    analogRead(adcPin); 
    delay(20);
    for (int loop = 0; loop < LIPO_READINGS; loop++) {
        reading += analogRead(adcPin); 
        delay(20);
    }
    return reading * V_REF / LIPO_READINGS / 1023.0 ;
}

float getVoltageCell1() {
   return  getVoltage(CELL1) * CELL1_CORRECTION_FACTOR;
}


float getVoltageCell2() {
   return  getVoltage(CELL2) * CELL2_CORRECTION_FACTOR;
}

void sendLipoStatus() {
    sendLipoStatus("");
}


void sendLipoStatus(const String& messageUid) {

    String message = "LIPO_INFO,,";

    message += messageUid;
    message += ",";
    message += getVoltageCell1();
    message += ",";
    message += getVoltageCell2();
    message = "$" + message + "*" + checkSum(message);
    Serial2.println(message);    
}
