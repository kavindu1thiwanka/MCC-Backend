package com.bms.util;

public abstract class CommonConstants {

    public static final String SYSTEM_SCHEDULER = "system_schedulers";

    public static final char STATUS_ACTIVE = 'A';
    public static final char STATUS_INACTIVE = 'I';
    public static final char STATUS_DELETE = 'D';
    public static final char STATUS_PAID = 'P';
    public static final char STATUS_NOT_PAID = 'N';
    public static final char STATUS_PARTIALLY_PAID = 'H';
    public static final char STATUS_SENT = 'H';
    public static final char STATUS_UNSENT = 'H';
    public static final char STATUS_FAILED = 'F';

    // Menu type constants
    public static final char MENU_TYPE_MAIN_MENU = 'M';
    public static final char MENU_TYPE_SUB_MENU = 'S';

    // Roles
    public static final Integer ROLE_ID_ADMIN = 1;
    public static final Integer ROLE_ID_CUSTOMER = 2;
    public static final Integer ROLE_ID_DRIVER = 3;

    public static final String ROLE_ADMIN = "RL_ADMIN";
    public static final String ROLE_CUSTOMER = "RL_CUSTOMER";
    public static final String ROLE_DRIVER = "RL_DRIVER";

    public static final String CHARACTER_ROLE_CUSTOMER = "BMSCR8754";
    public static final String CHARACTER_ROLE_DRIVER = "BMSCR4432";

    // YML Properties
    public static final String JWT_SECRET = "${jwt.secret}";
    public static final String YML_CORS_ALLOW_ORIGINS = "${application.cors-allow-origins}";
    public static final String MAX_RETRY_COUNT = "${spring.mail.retry-count}";
    public static final String FROM_MAIL = "${spring.mail.from}";

    //Date Format
    public static final String US_DATE_FORMATS_STRING = "MM/dd/yyyy";
    public static final String PHOTON_OCR_DATE_FORMAT_STRING = "yyyy-MM-dd";
    public static final String SEPERATOR_LESS_DATE_FORMATS_STRING = "MMddyyyy";
    public static final String UNATTENDED_PAYMENT_DATE_FORMAT = "yyy-MM-dd HH:mm:ss";
}
