/*
 * Checks the current from the motors of the mecanum drive and stops, if the current is greater than 2Amps
 */
void knightRider()
{
  static const unsigned long KNIGHTRIDER_REFRESH_INTERVAL = 250; // ms
  static unsigned long lastKRRefreshTime = 0;

  if (millis() - lastKRRefreshTime >= KNIGHTRIDER_REFRESH_INTERVAL)
  {
    lastKRRefreshTime += KNIGHTRIDER_REFRESH_INTERVAL;
    // the knight rider effect
    for (int i = 0; i < 4; i++) {
      analogWrite(ledPwmPins[i], ledSequence[ledSequenceIndex + i]);
    }
    if (ledSequenceDirection) {
      ledSequenceIndex++;
    } else {
      ledSequenceIndex--;
    }
    if (ledSequenceIndex >= 7) {
      ledSequenceDirection = !ledSequenceDirection;
    }
    if (ledSequenceIndex <= 0) {
      ledSequenceDirection = !ledSequenceDirection;
    }
  }
}
