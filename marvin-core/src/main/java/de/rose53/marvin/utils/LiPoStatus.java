package de.rose53.marvin.utils;

import java.util.NavigableMap;
import java.util.TreeMap;

public class LiPoStatus {

    private static final NavigableMap<Float, Integer> map = new TreeMap<>();

    static {
        map.put(3.27f,0);
        map.put(3.62f,5);
        map.put(3.69f,10);
        map.put(3.71f,15);
        map.put(3.73f,20);
        map.put(3.75f,25);
        map.put(3.77f,30);
        map.put(3.79f,35);
        map.put(3.80f,40);
        map.put(3.82f,45);
        map.put(3.84f,50);
        map.put(3.85f,55);
        map.put(3.87f,60);
        map.put(3.91f,65);
        map.put(3.95f,70);
        map.put(3.98f,75);
        map.put(4.02f,80);
        map.put(4.08f,85);
        map.put(4.11f,90);
        map.put(4.15f,95);
        map.put(4.20f,100);
    }

    private final float voltageCell1;
    private final float voltageCell2;


    public LiPoStatus(float voltageCell1, float voltageCell2) {
        this.voltageCell1 = voltageCell1;
        this.voltageCell2 = voltageCell2;
    }

    public float getVoltageCell1() {
        return voltageCell1;
    }

    public float getVoltageCell2() {
        return voltageCell2;
    }

    public float getVoltage() {
        return getVoltageCell1() + getVoltageCell2();
    }

    public int getStateOfCharge() {
        float mediumCellVoltage = getVoltage() / 2;
        if (mediumCellVoltage > 4.20f) {
            return 100;
        }
        if (mediumCellVoltage < 3.27f) {
            return 0;
        }
        return map.floorEntry(mediumCellVoltage).getValue();
    }
}
