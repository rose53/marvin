package de.rose53.marvin.platform;

import static org.junit.Assert.*;

import org.junit.Test;

import de.rose53.marvin.platform.message.HDGMessage;

/**
 *
 * @author rose
 */
public class MessageTest {

    public MessageTest() {
    }

    @Test
    public void testBuildNull() throws InvalidMessageException, ChecksumErrorMessageException {
        assertNull(Message.build(null));
    }

    @Test
    public void testBuildEmpty() throws InvalidMessageException, ChecksumErrorMessageException {
        assertNull(Message.build(""));
    }

    @Test(expected = InvalidMessageException.class)
    public void testBuildInvalidStart() throws InvalidMessageException, ChecksumErrorMessageException {
        assertNotNull(Message.build("US,01,,9*3e"));
    }

    @Test(expected = InvalidMessageException.class)
    public void testBuildInvalid() throws InvalidMessageException, ChecksumErrorMessageException {
        assertNotNull(Message.build("$US,01,,67"));
    }

    @Test(expected = InvalidMessageException.class)
    public void testBuildInvalidLegth() throws InvalidMessageException, ChecksumErrorMessageException {
        assertNotNull(Message.build("$US*06"));
    }

    @Test(expected = ChecksumErrorMessageException.class)
    public void testBuildChecksum() throws InvalidMessageException, ChecksumErrorMessageException {
        assertNotNull(Message.build("$US,01,,9*3f"));
    }

    @Test
    public void testBuild() throws InvalidMessageException, ChecksumErrorMessageException {
        Message message = Message.build("$US,01,,9*12");
        assertNotNull(message);
        assertEquals(EMessageType.US,message.getMessageType());
        assertEquals("01",message.getMessageId());
        assertEquals("",message.getMessageUid());
    }

    @Test
    public void testBuildMessageUid() throws InvalidMessageException, ChecksumErrorMessageException {
        Message message = Message.build("$US,01,0815,9*1e");
        assertNotNull(message);
        assertEquals(EMessageType.US,message.getMessageType());
        assertEquals("01",message.getMessageId());
        assertEquals("0815",message.getMessageUid());

    }

    @Test
    public void testBuildMessageFloat() throws InvalidMessageException, ChecksumErrorMessageException {
        Message message = Message.build("$HDG,12345,,6.07*49");
        assertNotNull(message);
        assertEquals(EMessageType.HDG,message.getMessageType());
        assertEquals("12345",message.getMessageId());
        assertEquals("",message.getMessageUid());
        assertEquals(6.07,((HDGMessage)message).getHeading(),0.001);
    }

    @Test
    public void testGenerateChecksum() {
        assertEquals("60",Message.generateChecksum("MEC,,,0,25,0"));
        assertEquals("60",Message.generateChecksum("MEC,,,0,25,0"));
    }
}
