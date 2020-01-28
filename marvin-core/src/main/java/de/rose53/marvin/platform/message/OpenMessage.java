package de.rose53.marvin.platform.message;

import de.rose53.marvin.platform.EMessageType;
import de.rose53.marvin.platform.Message;

public class OpenMessage extends Message {

    public static final OpenMessage OPEN = new OpenMessage();

    private OpenMessage() {
        super(EMessageType.OPEN,"");
    }

    @Override
    protected String getDataString() {
        return getMessageHeader();
    }

}
