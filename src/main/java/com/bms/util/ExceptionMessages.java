package com.bms.util;

public abstract class ExceptionMessages {

    // User Related Exceptions
    public static final String USERNAME_CANNOT_BE_EMPTY = "Username cannot be empty";
    public static final String USER_ID_CANNOT_BE_EMPTY = "User id cannot be empty";
    public static final String USER_STATUS_CANNOT_BE_EMPTY = "User status cannot be empty";
    public static final String USER_IDS_CANNOT_BE_EMPTY = "User id(s) cannot be empty";
    public static final String FIRST_NAME_CANNOT_BE_EMPTY = "First name cannot be empty";
    public static final String LAST_NAME_CANNOT_BE_EMPTY = "Last name cannot be empty";
    public static final String USER_EMAIL_CANNOT_BE_EMPTY = "Email cannot be empty";
    public static final String USER_CONTACT_NUMBER_CANNOT_BE_EMPTY = "Contact number cannot be empty";
    public static final String USER_PASSWORD_CANNOT_BE_EMPTY = "Password cannot be empty";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String USERNAME_ALREADY_EXISTS = "Username already exists";
    public static final String USER_ALREADY_EXISTS = "There is an existing user with the provided email address";
    public static final String DRIVER_LICENSE_NO_CANNOT_BE_EMPTY = "Driver license no cannot be empty";
    public static final String INVALID_PASSWORD = "Invalid password";
    public static final String IDENTIFIER_NOT_FOUND = "Identifier not found";
    public static final String INVALID_IDENTIFIER = "Invalid identifier";
    public static final String NEW_PASSWORD_SAME_AS_OLD_PASSWORD = "New password cannot be same as old password.";
    public static final String NEW_PASSWORD_CANNOT_BE_EMPTY = "New password cannot be empty.";

    // Role Related Exceptions
    public static final String ROLE_ID_CANNOT_BE_EMPTY = "Role id cannot be empty";
    public static final String ROLE_IDS_CANNOT_BE_EMPTY = "Role id(s) cannot be empty";
    public static final String ROLE_NOT_FOUND = "Role not found";

    // Reservation Related Exceptions
    public static final String DRIVER_ID_CANNOT_BE_EMPTY = "Driver id cannot be empty";
    public static final String PICK_UP_DATE_CANNOT_BE_EMPTY = "Pick up date cannot be empty";
    public static final String RETURN_DATE_CANNOT_BE_EMPTY = "Return date cannot be empty";
    public static final String PICK_UP_LOCATION_CANNOT_BE_EMPTY = "Pick up location cannot be empty";
    public static final String DRIVERS_NOT_AVAILABLE = "No drivers available at the moment";

    // Email Related Exceptions
    public static final String EMAIL_TEMPLATE_NOT_FOUND = "Email template not found";

    // Reservation & Payment Related Exceptions
    public static final String RESERVATION_NOT_FOUND = "Reservation not found";
    public static final String TRANSACTION_NOT_FOUND = "Transaction not found";

    // Vehicle Related Exceptions
    public static final String VEHICLE_ALREADY_EXISTS = "Vehicle already exists";
    public static final String VEHICLE_NO_CANNOT_BE_EMPTY = "Vehicle number cannot be empty";
    public static final String VEHICLE_DETAILS_CANNOT_BE_NULL = "Vehicle details cannot be null";
    public static final String VEHICLE_NAME_CANNOT_BE_EMPTY = "Vehicle name cannot be empty";
    public static final String VEHICLE_TYPE_CANNOT_BE_EMPTY = "Vehicle type cannot be empty";
    public static final String SEATS_AMOUNT_CANNOT_BE_NULL = "Seats amount cannot be null";
    public static final String GEAR_TYPE_CANNOT_BE_EMPTY = "Gear type cannot be empty";
    public static final String PRICE_PER_DAY_CANNOT_BE_NULL = "Price per day cannot be null";
    public static final String VEHICLE_NOT_FOUND = "Vehicle not found.";

}
