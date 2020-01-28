package de.rose53.marvin.platform.message;

import static de.rose53.marvin.platform.EMessageType.MEC_INFO;

import java.util.StringJoiner;

import de.rose53.marvin.ReadMecanumMotorInfo;
import de.rose53.marvin.platform.Message;

/**
 *
 * @author rose
 */
public class MECInfoMessage extends Message {

    private final boolean directionFL;
    private final short   speedFL;
    private final boolean directionRL;
    private final short   speedRL;
    private final boolean directionFR;
    private final short   speedFR;
    private final boolean directionRR;
    private final short   speedRR;

    public MECInfoMessage(boolean directionFL, short speedFL, boolean directionRL, short speedRL, boolean directionFR,
                          short speedFR, boolean directionRR, short speedRR) {
        super(MEC_INFO, "","");
        this.directionFL = directionFL;
        this.speedFL = speedFL;
        this.directionRL = directionRL;
        this.speedRL = speedRL;
        this.directionFR = directionFR;
        this.speedFR = speedFR;
        this.directionRR = directionRR;
        this.speedRR = speedRR;
    }
    @Override
    protected String getDataString() {
        StringJoiner sj = new StringJoiner(",");

        sj.add(getMessageHeader())
          .add(directionFL?"1":"0")
          .add(Short.toString(speedFL))
          .add(directionRL?"1":"0")
          .add(Short.toString(speedRL))
          .add(directionFR?"1":"0")
          .add(Short.toString(speedFR))
          .add(directionRR?"1":"0")
          .add(Short.toString(speedRR));

        return sj.toString();
    }

    public ReadMecanumMotorInfo[] getReadMecanumMotorInfo() {

        ReadMecanumMotorInfo[] retVal = new ReadMecanumMotorInfo[4];

        retVal[0] = new ReadMecanumMotorInfo(directionFL, speedFL);
        retVal[1] = new ReadMecanumMotorInfo(directionRL, speedRL);
        retVal[2] = new ReadMecanumMotorInfo(directionFR, speedFR);
        retVal[3] = new ReadMecanumMotorInfo(directionRR, speedRR);

        return retVal;
    }
}
