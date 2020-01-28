package de.rose53.marvin.platform;

import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.rose53.marvin.platform.message.HDGMessage;
import de.rose53.marvin.platform.message.MECCurrentMessage;
import de.rose53.marvin.platform.message.MECInfoMessage;
import de.rose53.marvin.platform.message.USMessage;

/**
 *
 * @author rose
 */
public abstract class Message {

    private static final Logger logger = LoggerFactory.getLogger(Message.class);

    private EMessageType messageType;
    private String messageId;
    private String messageUid;

    protected Message(EMessageType messageType, String messageId) {
        this(messageType,messageId,"");
    }

    protected Message(EMessageType messageType, String messageId, String messageUid) {
        this.messageType = messageType;
        this.messageId   = messageId;
        this.messageUid  = messageUid;
    }

    public static Message build(String messageString) throws InvalidMessageException, ChecksumErrorMessageException {

        if (StringUtils.isEmpty(messageString)) {
            return null;
        }

        if (!messageString.matches("\\$\\w+(?:,[a-zA-Z_0-9\\.]*)*\\*\\w\\w")) {
            logger.error("build: message >{}< does not match the regexp",messageString);
            throw new InvalidMessageException();
        }

        // find and remove the checksumm
        String work = messageString.substring(1, messageString.lastIndexOf('*'));

        if (!validateMessage(work,messageString.substring(messageString.lastIndexOf('*') + 1))) {
            logger.error("build: message >{}< has an checksum error",messageString);
            throw new ChecksumErrorMessageException();
        }

        String[] messageParts = work.split(",");
        if (messageParts.length < 3) {
            throw new InvalidMessageException();
        }
        Message retVal = null;
        // first part is message type
        if ("US".equalsIgnoreCase(messageParts[0])) {
            retVal = new USMessage(messageParts[1], Integer.parseInt(messageParts[3]));
        } else if ("HDG".equalsIgnoreCase(messageParts[0])) {
            retVal = new HDGMessage(messageParts[1],Float.parseFloat(messageParts[3]));
        } else if ("MEC_CURR".equalsIgnoreCase(messageParts[0])) {
            logger.debug("build: ");
            retVal = new MECCurrentMessage(Integer.parseInt(messageParts[3]), Integer.parseInt(messageParts[4]),
                                           Integer.parseInt(messageParts[5]), Integer.parseInt(messageParts[6]));
        } else if ("MEC_INFO".equalsIgnoreCase(messageParts[0])) {
            retVal = new MECInfoMessage(Integer.parseInt(messageParts[3]) > 0, Short.parseShort(messageParts[4]),
                                        Integer.parseInt(messageParts[5]) > 0, Short.parseShort(messageParts[6]),
                                        Integer.parseInt(messageParts[7]) > 0, Short.parseShort(messageParts[8]),
                                        Integer.parseInt(messageParts[9]) > 0, Short.parseShort(messageParts[10]));
        }


        if (retVal != null && StringUtils.isNotEmpty(messageParts[2])) {
            retVal.setMessageUid(messageParts[2]);
        }
        return retVal;
    }

    protected String getMessageHeader()  {
        StringJoiner sj = new StringJoiner(",");

        sj.add(getMessageType().toString())
          .add(getMessageId())
          .add(getMessageUid());

        return sj.toString();
    }

    public final String getMessageString() {

        String dataString = getDataString();

        StringBuilder sb = new StringBuilder("$");
        sb.append(dataString)
          .append('*')
          .append(generateChecksum(dataString));

        logger.trace("getMessageString: message = >{}<",sb.toString());
        return sb.toString();
    }

    public final String getTerminatedMessageString() {
        return getMessageString() + '\n';
    }

    protected abstract String getDataString();

    public EMessageType getMessageType() {
        return messageType;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessageUid() {
        return messageUid;
    }

    public void setMessageUid(String messageUid) {
        this.messageUid = messageUid;
    }

    static public String generateChecksum(String data) {
        int t = 0;
        for (char c : data.toCharArray()) {
            t = t ^ c;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toHexString(t));
        if (sb.length() < 2) {
            sb.insert(0, '0'); // pad with leading zero if needed
        }
        return sb.toString();
    }

    static public boolean validateMessage(String data, String checksum) {
        return generateChecksum(data).equalsIgnoreCase(checksum);
    }

}
