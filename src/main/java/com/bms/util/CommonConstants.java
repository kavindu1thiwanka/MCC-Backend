package com.bms.util;

public abstract class CommonConstants {

    public static final String SYSTEM_SCHEDULER = "system_schedulers";

    public static final char STATUS_ACTIVE = 'A';
    public static final char STATUS_INACTIVE = 'I';
    public static final char STATUS_DELETE = 'D';
    public static final char STATUS_PAID = 'P';
    public static final char STATUS_TRANSACTION_PENDING = 'P';
    public static final char STATUS_TRANSACTION_COMPLETE = 'C';
    public static final char STATUS_NOT_PAID = 'N';
    public static final char STATUS_PARTIALLY_PAID = 'H';
    public static final char STATUS_SENT = 'S';
    public static final char STATUS_UNSENT = 'U';
    public static final char STATUS_FAILED = 'F';

    public static final String EMPTY_STRING = "";
    public static final String EMPTY_SPACE_STRING = " ";
    public static final String CHARACTER_TYPE = "ISO-8859-1";
    public static final String HREF_ATTR = "href";

    // Menu type constants
    public static final char MENU_TYPE_MAIN_MENU = 'M';
    public static final char MENU_TYPE_SUB_MENU = 'S';

    // Roles
    public static final int ROLE_ID_ADMIN = 1;
    public static final int ROLE_ID_CUSTOMER = 2;
    public static final int ROLE_ID_DRIVER = 3;

    public static final String ROLE_ADMIN = "RL_ADMIN";
    public static final String ROLE_CUSTOMER = "RL_CUSTOMER";
    public static final String ROLE_DRIVER = "RL_DRIVER";

    public static final String IDENTIFIER_ROLE_ADMIN = "LTWRHaJVNMKk";
    public static final String IDENTIFIER_ROLE_CUSTOMER = "mGwDgRbpBKyf";
    public static final String IDENTIFIER_ROLE_DRIVER = "EBWSfvxfmWpr";

    // YML Properties
    public static final String JWT_SECRET = "${jwt.secret}";
    public static final String YML_CORS_ALLOW_ORIGINS = "${application.cors-allow-origins}";
    public static final String MAX_RETRY_COUNT = "${spring.mail.retry-count}";
    public static final String FROM_MAIL = "${spring.mail.from}";
    public static final String CONFIRM_USER_EMAIL_URL = "${application.url.confirm-email}";

    // Date Format
    public static final String US_DATE_FORMATS_STRING = "MM/dd/yyyy";
    public static final String PHOTON_OCR_DATE_FORMAT_STRING = "yyyy-MM-dd";
    public static final String SEPERATOR_LESS_DATE_FORMATS_STRING = "MMddyyyy";
    public static final String UNATTENDED_PAYMENT_DATE_FORMAT = "yyy-MM-dd HH:mm:ss";

    // Email Template
    public static final Integer EMAIL_TEMPLATE_CONFIGURE_USER = 1;

    // Params
    public static final String PARAM_EMAIL_SEND_TO = "EMAIL_SEND_TO";
    public static final String PARAM_CONFIGURATION_URL = "CONFIGURATION_URL";
    public static final String PARAM_UUID = "UUID";
    public static final String PARAM_ID = "PARAM_ID";

    // SQL Query Constants
    public static final String SQL_AND = "AND";
    public static final String SQL_OR = "OR";
    public static final String SQL_ORDER_BY = "ORDER BY";

    // Payment Type
    public static final Integer PAYMENT_TYPE_CARD = 1;
    public static final Integer PAYMENT_TYPE_BANK = 2;

    // Other
    public static final String STRING_CURRENCY = "currency";
    public static final String STRING_AMOUNT = "amount";
    public static final String STRING_TRANSACTION_ID = "transactionId";
}
