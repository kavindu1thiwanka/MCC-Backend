package com.bms.controller.abst;

public abstract class Mappings {

    // Request Mappings
    public static final String AUTH = "/auth";
    public static final String USER = "/user";
    public static final String DRIVER = "/driver";
    public static final String ADMIN = "/admin";
    public static final String ROLE = "/role";
    public static final String MENU = "/menu";
    public static final String VEHICLE = "/vehicle";
    public static final String RESERVATION = "/res";
    public static final String PAYMENTS = "/payments";

    // Version Mappings
    public static final String VERSION_V1 = "/v1";

    // Auth Mappings
    public static final String LOGIN_V1 = VERSION_V1 + "/login";
    public static final String REFRESH_V1 = VERSION_V1 + "/refresh";
    public static final String SEND_PASSWORD_RESET_MAIL_V1 = VERSION_V1 + "/send_pws_reset";

    // User Management Mappings
    public static final String REGISTER_USER_V1 = VERSION_V1 + "/register";
    public static final String CREATE_USER_V1 = VERSION_V1 + "/create";
    public static final String CONFIRM_USER_EMAIL_V1 = VERSION_V1 + "/confirm";
    public static final String UPDATE_USER_V1 = VERSION_V1 + "/update_user";
    public static final String CHANGE_USER_STATUS_V1 = VERSION_V1 + "/change_user_status";
    public static final String GET_ALL_USERS_V1 = VERSION_V1 + "/get_all_users";
    public static final String GET_ALL_DRIVERS_V1 = VERSION_V1 + "/get_all_drivers";
    public static final String GET_ALL_ADMIN_V1 = VERSION_V1 + "/get_all_admins";
    public static final String GET_USER_DETAILS_V1 = VERSION_V1 + "/get_user_details";
    public static final String GET_USER_ADDRESS_V1 = VERSION_V1 + "/get_user_address";
    public static final String UPDATE_USER_ADDRESS_V1 = VERSION_V1 + "/update_user_address";
    public static final String RESET_PASSWORD_V1 = VERSION_V1 + "/reset_password";
    public static final String GET_LOGGED_IN_USER_DETAILS_V1 = VERSION_V1 + "/get_logged_in_user_details";

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
    public static final String GET_VEHICLE_TOTAL_COST = VERSION_V1 + "/get_vehicle_total_cost";
    public static final String ADD_VEHICLE_V1 = VERSION_V1 + "/add_vehicle";
    public static final String UPDATE_VEHICLE_V1 = VERSION_V1 + "/update_vehicle";
    public static final String UPDATE_VEHICLE_STATUS_V1 = VERSION_V1 + "/update_vehicle_status";
    public static final String GET_ALL_VEHICLE_LIST_V1 = VERSION_V1 + "/get_all_vehicle_list";
    public static final String GET_VEHICLE_DETAILS_V1 = VERSION_V1 + "/get_vehicle_details";

    // Menu Management Mappings
    public static final String GET_MENU_LIST_V1 = VERSION_V1 + "/get_menu_list";

    // Reservation Management Mappings
    public static final String CREATE_RESERVATION_V1 = VERSION_V1 + "/create_reservation";
    public static final String UPDATE_RESERVATION_DETAILS_V1 = VERSION_V1 + "/update_reservation_details";
    public static final String GET_RESERVATION_DETAILS_V1 = VERSION_V1 + "/get_reservation_details";
    public static final String UPDATE_RESERVATION_STATUS_V1 = VERSION_V1 + "/update_reservation_status";

    // Payment Management Mappings
    public static final String CREATE_PAYMENT_SESSION = "/create-payment-session";

    // Admin Management Mappings
    public static final String LOAD_ADMIN_DASHBOARD_DETAILS_V1 = VERSION_V1 + "/load_admin_dashboard_details";

    // Driver Management Mappings
    public static final String UPDATE_ONLINE_STATUS_V1 = VERSION_V1 + "/update_online_status";
    public static final String GET_ONLINE_STATUS_V1 = VERSION_V1 + "/get_dashboard_details";
    public static final String GET_RIDES_HISTORY_V1 = VERSION_V1 + "/get_rides_history";
}
