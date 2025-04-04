package com.bms.entity;

import com.bms.dto.UserDto;
import com.bms.entity.abst.CommonBaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

import static com.bms.util.CommonConstants.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "user_mst")
@EqualsAndHashCode(callSuper = true)
public class UserMst extends CommonBaseEntity implements Serializable {

    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "role_id", nullable = false)
    private Integer roleId;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "driver_license_no")
    private String driverLicenseNo;

    @Column(name = "driver_license_url")
    private String driverLicenseUrl;

    @Column(name = "is_online")
    private Character isOnline;

    @Column(name = "on_trip")
    private Boolean onTrip;

    @Column(name = "status", nullable = false)
    private Character status;

    private transient boolean isLoggedInProfileUpdated;

    public UserMst(UserDto user) {
        this.uuid = UUID.randomUUID().toString();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.username = this.email;
        this.contactNumber = user.getContactNumber();
        this.driverLicenseNo = user.getDriverLicenseNo();
        this.status = STATUS_INACTIVE;

        switch (user.getIdentifier()) {
            case IDENTIFIER_ROLE_CUSTOMER:
                this.roleId = ROLE_ID_CUSTOMER;
                break;
            case IDENTIFIER_ROLE_DRIVER:
                this.roleId = ROLE_ID_DRIVER;
                break;
            case IDENTIFIER_ROLE_ADMIN:
                this.roleId = ROLE_ID_ADMIN;
                break;
        }

        if (this.roleId == ROLE_ID_DRIVER) {
            this.isOnline = STATUS_NO;
        }
    }

    public void updateUserDetails(UserDto userDetails) {
        this.firstName = userDetails.getFirstName();
        this.lastName = userDetails.getLastName();
        this.email = userDetails.getEmail();
        this.contactNumber = userDetails.getContactNumber();
        this.driverLicenseNo = userDetails.getDriverLicenseNo();
    }
}
