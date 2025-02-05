package com.bms.controller.abst;

public abstract class Mappings {

    // Request Mappings
    public static final String AUTH = "/auth";
    public static final String USER = "/user";
    public static final String ROLE = "/role";
    public static final String MENU = "/menu";
    public static final String VEHICLE = "/vehicle";
    public static final String RESERVATION = "/res";

    // Version Mappings
    public static final String VERSION_V1 = "/v1";

    // Auth Mappings
    public static final String LOGIN_V1 = VERSION_V1 + "/login";

    // User Management Mappings
    public static final String CREATE_USER_V1 = VERSION_V1 + "/create_user";

    // Role Management Mappings
    public static final String CREATE_ROLE_V1 = VERSION_V1 + "/create_role";
    public static final String GET_ALL_ROLES_V1 = VERSION_V1 + "/get_all_roles";
    public static final String GET_ROLE_PRIVILEGES_V1 = VERSION_V1 + "/get_role_privileges";
    public static final String ACTIVATE_ROLE_V1 = VERSION_V1 + "/sec_activate_role";
    public static final String ACTIVATE_ROLE_BULK_V1 = VERSION_V1 + "/sec_activate_role_bulk";
    public static final String INACTIVATE_ROLE_V1 = VERSION_V1 + "/sec_inactivate_role";
    public static final String INACTIVATE_ROLE_BULK_V1 = VERSION_V1 + "/sec_inactivate_role_bulk";
    public static final String DELETE_ROLE_V1 = VERSION_V1 + "/sec_delete_role";
    public static final String DELETE_ROLE_BULK_V1 = VERSION_V1 + "/sec_delete_role_bulk";
    public static final String GET_USER_PRIVILEGES_V1 = VERSION_V1 + "/get_user_privileges";

    // Vehicle Management Mappings
    public static final String GET_VEHICLE_LIST_V1 = VERSION_V1 + "/get_vehicle_list";

    // Menu Management Mappings
    public static final String GET_MENU_LIST_V1 = VERSION_V1 + "/get_menu_list";

    // Reservation Management Mappings
    public static final String CREATE_RESERVATION_V1 = VERSION_V1 + "/sec_create_reservation";
}
