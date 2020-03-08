void sendDistance() {
    sendDistance("","FRONT");
    sendDistance("","LEFT");
    sendDistance("","RIGHT");
    sendDistance("","BACK");
}

float distance(const String& place) {

    long pulse;
    if (place == "FRONT") {
        return sonar.ping_cm();
    } else if (place == "LEFT") {
        long pulse = pulseIn(PWM_MAXBOTIX_0, HIGH);
        return pulse / 147 * 2.54;
    } else if (place == "RIGHT") {
        long pulse = pulseIn(PWM_MAXBOTIX_1, HIGH);
        return pulse / 147 * 2.54;
    } else if (place == "BACK") {
        long pulse = pulseIn(PWM_MAXBOTIX_2, HIGH);
        return pulse / 147 * 2.54;
    }

    return 0.0;
}

void sendDistance(const String& messageUid, const String& place) {

    String message = "US,";

    message += place;
    message += ",";
    message += messageUid;
    message += ",";
    message += distance(place);

    message = "$" + message + "*" + checkSum(message);
    Serial2.println(message);
}
