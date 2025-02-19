package com.bms.controller.abst;

public abstract class Mappings {

    // Request Mappings
    public static final String AUTH = "/auth";
    public static final String USER = "/user";
    public static final String ROLE = "/role";
    public static final String MENU = "/menu";
    public static final String VEHICLE = "/vehicle";
    public static final String RESERVATION = "/res";
    public static final String PAYMENTS = "/payments";

    // Version Mappings
    public static final String VERSION_V1 = "/v1";

    // Auth Mappings
    public static final String LOGIN_V1 = VERSION_V1 + "/login";

    // User Management Mappings
    public static final String REGISTER_USER_V1 = VERSION_V1 + "/register";
    public static final String CONFIRM_USER_EMAIL_V1 = VERSION_V1 + "/confirm";
    public static final String UPDATE_USER_V1 = VERSION_V1 + "/update_user";
    public static final String ACTIVATE_USER_V1 = VERSION_V1 + "/activate_user";
    public static final String ACTIVATE_USER_BULK_V1 = VERSION_V1 + "/activate_user_bulk";
    public static final String INACTIVATE_USER_V1 = VERSION_V1 + "/inactivate_user";
    public static final String INACTIVATE_USER_BULK_V1 = VERSION_V1 + "/inactivate_user_bulk";
    public static final String DELETE_USER_V1 = VERSION_V1 + "/delete_user";
    public static final String DELETE_USER_BULK_V1 = VERSION_V1 + "/delete_user_bulk";
    public static final String GET_ALL_USERS_V1 = VERSION_V1 + "/get_all_users";
    public static final String GET_USER_DETAILS_V1 = VERSION_V1 + "/get_user_details";
    public static final String IS_ADDRESS_AVAILABLE_V1 = VERSION_V1 + "/is_address_available";

    // Role Management Mappings
    public static final String CREATE_ROLE_V1 = VERSION_V1 + "/create_role";
    public static final String GET_ALL_ROLES_V1 = VERSION_V1 + "/get_all_roles";
    public static final String GET_ROLE_PRIVILEGES_V1 = VERSION_V1 + "/get_role_privileges";
    public static final String ACTIVATE_ROLE_V1 = VERSION_V1 + "/activate_role";
    public static final String ACTIVATE_ROLE_BULK_V1 = VERSION_V1 + "/activate_role_bulk";
    public static final String INACTIVATE_ROLE_V1 = VERSION_V1 + "/inactivate_role";
    public static final String INACTIVATE_ROLE_BULK_V1 = VERSION_V1 + "/inactivate_role_bulk";
    public static final String DELETE_ROLE_V1 = VERSION_V1 + "/delete_role";
    public static final String DELETE_ROLE_BULK_V1 = VERSION_V1 + "/delete_role_bulk";
    public static final String GET_USER_PRIVILEGES_V1 = VERSION_V1 + "/get_user_privileges";

    // Vehicle Management Mappings
    public static final String GET_VEHICLE_LIST_V1 = VERSION_V1 + "/get_vehicle_list";

    // Menu Management Mappings
    public static final String GET_MENU_LIST_V1 = VERSION_V1 + "/get_menu_list";

    // Reservation Management Mappings
    public static final String CREATE_RESERVATION_V1 = VERSION_V1 + "/create_reservation";

    // Payment Management Mappings
    public static final String CREATE_PAYMENT_SESSION = "/create-payment-session";
}
