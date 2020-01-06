
/*
 *
 */
void readCommand65() {
  uint8_t buffer[12];
  
  int tmpSpeed = mecanumDrive.getSpeed(FRONT_LEFT);
  buffer[2] = tmpSpeed >> 8;
  buffer[1] = tmpSpeed & 0xff;
  buffer[0] = mecanumDrive.getMotorDirection(FRONT_LEFT);
  
  tmpSpeed = mecanumDrive.getSpeed(REAR_LEFT);
  buffer[5] = tmpSpeed >> 8;
  buffer[4] = tmpSpeed & 0xff;
  buffer[3] = mecanumDrive.getMotorDirection(REAR_LEFT);
  
  tmpSpeed = mecanumDrive.getSpeed(FRONT_RIGHT);
  buffer[8] = tmpSpeed >> 8;
  buffer[7] = tmpSpeed & 0xff;
  buffer[6] = mecanumDrive.getMotorDirection(FRONT_RIGHT);
  
  tmpSpeed = mecanumDrive.getSpeed(REAR_RIGHT);
  buffer[11] = tmpSpeed >> 8;
  buffer[10] = tmpSpeed & 0xff;
  buffer[9] = mecanumDrive.getMotorDirection(REAR_RIGHT);
  
  Wire.write(buffer,12);
}

void readCommand66() {
  
  uint8_t buffer[2];
  buffer[1] = distance >> 8;
  buffer[0] = distance & 0xff;
  
  Wire.write(buffer, 2);
}
