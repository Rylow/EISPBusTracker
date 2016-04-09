package com.rylow.eispbustracker.network;

/**
 * Created by s.bakhti on 30.3.2016.
 */
public class TransmissionCodes {

    public static final int USER_LOGIN = 101;
    public static final int USER_LOGIN_REPLY_SUCCESS = 102;
    public static final int USER_LOGIN_REPLY_FAIL = 103;
    public static final int REQUEST_LINE_LIST = 104;
    public static final int RESPONSE_LINE_LIST = 105;
    public static final int REQUEST_RIDE_LIST = 106;
    public static final int RESPONCE_RIDE_LIST = 107;
    public static final int REQUEST_RIDE_DETAILS = 108;
    public static final int RESPONCE_RIDE_DETAILS = 109;
    public static final int GPS_UPDATE = 110;
    public static final int RIDE_DETAILS_UPDATE = 111;
    public static final int RIDE_DETAILS_CONFIRMATION = 112;
}
