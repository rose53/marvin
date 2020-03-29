package de.rose53.marvin.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class LiPoStatusTest {

    @Test
    public void getStateOfCharge() {
        LiPoStatus liPoStatus = new LiPoStatus(4.18f, 4.21f);
        assertNotNull(liPoStatus);

        assertEquals(95,liPoStatus.getStateOfCharge());
    }

    @Test
    public void getStateOfChargeUpperLimit() {
        LiPoStatus liPoStatus = new LiPoStatus(4.21f, 4.21f);
        assertNotNull(liPoStatus);

        assertEquals(100,liPoStatus.getStateOfCharge());
    }

    @Test
    public void getStateOfChargeLowerLimit() {
        LiPoStatus liPoStatus = new LiPoStatus(3.27f, 3.27f);
        assertNotNull(liPoStatus);

        assertEquals(0,liPoStatus.getStateOfCharge());
    }

}
