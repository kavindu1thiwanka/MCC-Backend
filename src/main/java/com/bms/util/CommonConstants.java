package com.bms.util;

public abstract class CommonConstants {

    public static final String SYSTEM_SCHEDULER = "system_schedulers";

    public static final char STATUS_ACTIVE = 'A';
    public static final char STATUS_INACTIVE = 'I';
    public static final char STATUS_DELETE = 'D';
    public static final char STATUS_PAID = 'P';
    public static final char STATUS_TRANSACTION_PENDING = 'P';
    public static final char STATUS_RESERVATION_CANCELLED = 'D';
    public static final char STATUS_COMPLETE = 'C';
    public static final char STATUS_NOT_PAID = 'N';
    public static final char STATUS_PARTIALLY_PAID = 'H';
    public static final char STATUS_SENT = 'S';
    public static final char STATUS_UNSENT = 'U';
    public static final char STATUS_FAILED = 'F';
    public static final char STATUS_YES = 'Y';
    public static final char STATUS_NO = 'N';

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

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_CUSTOMER = "CUSTOMER";
    public static final String ROLE_DRIVER = "DRIVER";

    public static final String ROLE_ADMIN_WITH_ROLE_PREFIX = "ROLE_ADMIN";
    public static final String ROLE_CUSTOMER_WITH_ROLE_PREFIX = "ROLE_CUSTOMER";
    public static final String ROLE_DRIVER_WITH_ROLE_PREFIX = "ROLE_DRIVER";

    public static final String IDENTIFIER_ROLE_ADMIN = "LTWRHaJVNMKk";
    public static final String IDENTIFIER_ROLE_CUSTOMER = "mGwDgRbpBKyf";
    public static final String IDENTIFIER_ROLE_DRIVER = "EBWSfvxfmWpr";

    // YML Properties
    public static final String JWT_SECRET = "${jwt.secret}";
    public static final String YML_CORS_ALLOW_ORIGINS = "${application.cors-allow-origins}";
    public static final String MAX_RETRY_COUNT = "${spring.mail.retry-count}";
    public static final String FROM_MAIL = "${spring.mail.from}";
    public static final String CONFIRM_USER_EMAIL_URL = "${application.url.confirm-email}";
    public static final String LOGIN_URL = "${application.url.login}";
    public static final String PWD_RESET_URL = "${application.url.pwd-reset}";
    public static final String GCP_PROJECT_ID = "${gcp.project-id}";
    public static final String GCP_BUCKET = "${gcp.bucket-name}";

    // Date Format
    public static final String US_DATE_FORMATS_STRING = "MM/dd/yyyy";
    public static final String PHOTON_OCR_DATE_FORMAT_STRING = "yyyy-MM-dd";
    public static final String SEPERATOR_LESS_DATE_FORMATS_STRING = "MMddyyyy";
    public static final String UNATTENDED_PAYMENT_DATE_FORMAT = "yyy-MM-dd HH:mm:ss";

    // Email Template
    public static final Integer EMAIL_TEMPLATE_CONFIGURE_USER = 1;
    public static final Integer EMAIL_TEMPLATE_REGISTRATION_SUCCESS = 2;
    public static final Integer EMAIL_TEMPLATE_PWD_RESET = 3;
    public static final Integer EMAIL_TEMPLATE_RESERVATION_SUCCESSFUL = 4;

    // Params
    public static final String PARAM_EMAIL_SEND_TO = "EMAIL_SEND_TO";
    public static final String PARAM_CONFIGURATION_URL = "CONFIGURATION_URL";
    public static final String PARAM_PASSWORD_RESET_URL = "PWD_RESET_URL";
    public static final String PARAM_LOGIN_URL = "LOGIN_URL";
    public static final String PARAM_UUID = "UUID";
    public static final String PARAM_ID = "PARAM_ID";
    public static final String PARAM_USERNAME = "USERNAME";
    public static final String PARAM_PWD_RESET_TOKEN = "PWD_RESET_TOKEN";
    public static final String PARAM_RESERVATION_ID = "RESERVATION_ID";
    public static final String PARAM_VEHICLE_MODEL = "VEHICLE_MODEL";
    public static final String PARAM_PICKUP_LOCATION = "PICKUP_LOCATION";
    public static final String PARAM_DROPOFF_LOCATION = "DROPOFF_LOCATION";
    public static final String PARAM_PICKUP_DATE = "PICKUP_DATE";
    public static final String PARAM_DROPOFF_DATE = "DROPOFF_DATE";
    public static final String PARAM_TOTAL_PRICE = "TOTAL_PRICE";

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
