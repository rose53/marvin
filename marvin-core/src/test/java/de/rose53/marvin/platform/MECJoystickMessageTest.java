package de.rose53.marvin.platform;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rose
 */
public class MECJoystickMessageTest {

    public MECJoystickMessageTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetMessageString() {
        byte ch1 = 100;
        byte ch3 = -100;
        byte ch4 = 0;
        MECJoystickMessage instance = new MECJoystickMessage(ch1, ch3, ch4);
        String expResult = "$MEC,,,100,-100,0*7a";
        String result = instance.getMessageString();
        assertEquals(expResult, result);
    }

}
