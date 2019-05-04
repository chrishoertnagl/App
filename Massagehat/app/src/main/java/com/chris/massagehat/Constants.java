package com.chris.massagehat;

public class Constants {
    public static final String DEVICE_ID = "EF:EA:F0:C2:F5:5E";
    public static final String SERVICE_NAME = "713D0000-503E-4C75-BA94-3148F18D941E";
    public static final String CHARACTERISTIC_ID = "713D0003-503E-4C75-BA94-3148F18D941E";

    public static final String CHARACTERISTIC_READ1 = "713D0001-503E-4C75-BA94-3148F18D941E";
    public static final String CHARACTERISTIC_READ2 = "713D0002-503E-4C75-BA94-3148F18D941E";

    public static final String TAG = "Massagehat";

    public static final int OFF = 0x0;
    public static final int STRONG = 0xff;
    public static final int MEDIUM = 0xff;
    public static final int WEAK = 0xff;

    public static final int RELAX = 1;
    public static final int ACTION = 2;
    public static final int WAKEUP = 3;
    public static final int CRAZY = 4;


}
