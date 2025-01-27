package com.bms.util;

public abstract class CommonConstant {

    public static final char STATUS_ACTIVE = 'A';
    public static final char STATUS_INACTIVE = 'I';
    public static final char STATUS_DELETE = 'D';

    // Menu type constants
    public static final char MENU_TYPE_MAIN_MENU = 'M';
    public static final char MENU_TYPE_SUB_MENU = 'S';

    // Roles
    public static final Integer ROLE_ID_ADMIN = 1;
    public static final Integer ROLE_ID_TEACHER = 2;
    public static final Integer ROLE_ID_STUDENT = 3;

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_TEACHER = "TEACHER";
    public static final String ROLE_STUDENT = "STUDENT";

    // YML Properties
    public static final String JWT_SECRET = "${jwt.secret}";
    public static final String YML_CORS_ALLOW_ORIGINS = "${application.cors-allow-origins}";
}
