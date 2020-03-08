package de.rose53.marvin.platform.message;

import java.util.StringJoiner;

import de.rose53.marvin.platform.EMessageType;
import de.rose53.marvin.platform.Message;

/**
 *
 * @author rose
 */
public class PanTiltIncMessage extends Message {

    private final short inc;

    private PanTiltIncMessage(EMessageType messageType, short inc) {
        super(messageType, "","");
        this.inc = inc;
    }

    public static PanTiltIncMessage buildPanIncMessage(short inc) {
        return new PanTiltIncMessage(EMessageType.PAN_INC, inc);
    }

    public static PanTiltIncMessage buildTiltIncMessage(short inc) {
        return new PanTiltIncMessage(EMessageType.TILT_INC, inc);
    }

    @Override
    protected String getDataString() {
        StringJoiner sj = new StringJoiner(",");

        sj.add(getMessageHeader())
          .add(Short.toString(inc));

        return sj.toString();
   }
}
