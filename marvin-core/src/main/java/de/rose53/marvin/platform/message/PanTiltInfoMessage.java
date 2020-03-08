package de.rose53.marvin.platform.message;

import java.util.StringJoiner;

import de.rose53.marvin.platform.EMessageType;
import de.rose53.marvin.platform.Message;

/**
 *
 * @author rose
 */
public class PanTiltInfoMessage extends Message {

    private final short pan;
    private final short tilt;

    public PanTiltInfoMessage(String messageId, short pan, short tilt) {
        super(EMessageType.PAN_TILT_INFO,messageId);
        this.pan  = pan;
        this.tilt = tilt;
    }

    public short getPan() {
        return pan;
    }

    public short getTilt() {
        return tilt;
    }

    @Override
    protected String getDataString() {
        StringJoiner sj = new StringJoiner(",");

        sj.add(getMessageHeader())
          .add(Short.toString(getPan()))
          .add(Short.toString(getTilt()));

        return sj.toString();
    }

}
