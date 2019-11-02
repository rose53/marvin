/*
 * Checks the current from the motors of the mecanum drive and stops, if the current is greater than 2Amps
 */
void checkCurrent()
{
  static const unsigned long CURRENT_REFRESH_INTERVAL = 1000; // ms
  static unsigned long lastRefreshTime = 0;

  if (millis() - lastRefreshTime >= CURRENT_REFRESH_INTERVAL)
  {
    lastRefreshTime += CURRENT_REFRESH_INTERVAL;
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
}
