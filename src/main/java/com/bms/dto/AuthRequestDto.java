package com.bms.dto;

import com.bms.entity.UserMst;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.bms.util.CommonConstants.*;

@Data
@NoArgsConstructor
public class AuthRequestDto {
    private String username;
    private String password;
    private String identifier;
    private String firstName;
    private String lastName;
    private String accessToken;
    private String refreshToken;

    public AuthRequestDto(String accessToken, String refreshToken) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }

    public AuthRequestDto(String accessToken, String refreshToken, UserMst user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
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
}
