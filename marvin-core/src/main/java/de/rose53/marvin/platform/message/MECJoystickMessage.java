package de.rose53.marvin.platform.message;

import java.util.StringJoiner;

import de.rose53.marvin.platform.EMessageType;
import de.rose53.marvin.platform.Message;

/**
 *
 * @author rose
 */
public class MECJoystickMessage extends Message {

    private final byte ch1;
    private final byte ch3;
    private final byte ch4;

    public MECJoystickMessage(byte ch1, byte ch3, byte ch4) {
        super(EMessageType.MEC, "","");
        this.ch1 = ch1;
        this.ch3 = ch3;
        this.ch4 = ch4;
    }

    @Override
    protected String getDataString() {
        StringJoiner sj = new StringJoiner(",");

        sj.add(getMessageHeader())
          .add(Byte.toString(ch1))
          .add(Byte.toString(ch3))
          .add(Byte.toString(ch4));

        return sj.toString();
   }
}
