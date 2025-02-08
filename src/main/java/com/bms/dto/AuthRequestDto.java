package com.bms.dto;

import com.bms.entity.UserMst;

import static com.bms.util.CommonConstants.*;

public class AuthRequestDto {
    private String username;
    private String password;
    private String token;
    private String identifier;
    private String firstName;
    private String lastName;

    public AuthRequestDto() {
    }

    public AuthRequestDto(String token, UserMst user) {
        this.token = token;
        this.username = null;
        this.password = null;
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();

        if (user.getRoleId().equals(ROLE_ID_ADMIN)) {
            this.identifier = IDENTIFIER_ROLE_ADMIN;
        } else if (user.getRoleId().equals(ROLE_ID_CUSTOMER)) {
            this.identifier = IDENTIFIER_ROLE_CUSTOMER;
        } else if (user.getRoleId().equals(ROLE_ID_DRIVER)) {
            this.identifier = IDENTIFIER_ROLE_DRIVER;
        }
    }

    public AuthRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
