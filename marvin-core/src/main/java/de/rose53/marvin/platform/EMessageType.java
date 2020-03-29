package de.rose53.marvin.platform;

/**
 *
 * @author rose
 */
public enum EMessageType {
    OPEN,              // open
    CLOSE,             // close
    US,                // ultrasonic sensor
    GET_US,            // Sends a message to read the US data
    HDG,               // Magnetometer/Compass heading
    GET_HDG,           // sends a message to read the heading
    MEC,               // Mecanum drive
    MEC_INFO,          // Mecanum drive info
    GET_MEC_INFO,      // Sends a message to read the Mecanum drive info
    MEC_CURR,          // Mecanum drive current
    GET_MEC_CURR,      // Sends a message to read the current
    PAN,               // Sends a message to set the angle of the pan servo
    PAN_INC,           // Sends a message to inc/dec the angle of the pan servo
    TILT,              // Sends a message to set the angle of the tilt servo
    TILT_INC,          // Sends a message to inc/dec the angle of the tilt servo
    PAN_TILT_INFO,     // pan/tilt actual angle info
    GET_PAN_TILT_INFO, // Sends a message to read the actual pan/tilt angle
    LIPO_INFO,         // LiPo battery info
    GET_LIPO_INFO      // Sends a message to read the LiPo battery info
}
