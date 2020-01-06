package de.rose53.marvin.platform;

import java.util.StringJoiner;

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

        sj.add(getMessageType().toString())
          .add(getMessageId())
          .add(getMessageUid())
          .add(Byte.toString(ch1))
          .add(Byte.toString(ch3))
          .add(Byte.toString(ch4));

        return sj.toString();
   }
}
