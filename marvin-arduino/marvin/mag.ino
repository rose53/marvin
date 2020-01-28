
void sendHeading() {
    sendHeading("");
}


void sendHeading(const String& messageUid) {
    sensors_event_t event; 
    mag.getEvent(&event);

    float heading = atan2(event.magnetic.y, event.magnetic.x) + PI / 2;

    if(heading < 0) {
        heading += 2*PI;
    }

    if(heading > 2*PI) {
        heading -= 2*PI;
    }

    // Convert radians to degrees for readability.
    float headingDegrees = heading * 180/M_PI; 
    
    String message = "HDG,,";

    message += messageUid;
    message += ",";
    message += headingDegrees;

    message = "$" + message + "*" + checkSum(message);
    Serial2.println(message);    
}
