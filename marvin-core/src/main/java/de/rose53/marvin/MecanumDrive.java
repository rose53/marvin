package de.rose53.marvin;

/**
 *
 * @author rose
 */
public interface MecanumDrive {

    /**
     * Sets the three values representing a joystick driven approach
     * @param ch1 factor for turning left / right
     * @param ch3 factor for moving forward / backward
     * @param ch4 factor for moving left / right
     */
    void mecanumDrive(byte ch1, byte ch3, byte ch4);

    short[] getCurrent();

    ReadMecanumMotorInfo[] getReadMecanumMotorInfo();
}
