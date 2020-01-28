

void sendDistance() {

    String message = "US,,";

    message += "";
    message += ",";
    message += sonar.ping_cm();

    message = "$" + message + "*" + checkSum(message);
    Serial2.println(message);
}
