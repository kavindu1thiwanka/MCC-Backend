package com.bms.entity;

import com.bms.dto.UserDto;
import com.bms.entity.abst.CommonBaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static com.bms.util.CommonConstants.STATUS_ACTIVE;

@Entity
@Data
@NoArgsConstructor
@Table(name = "user_mst")
@EqualsAndHashCode(callSuper = true)
public class UserMst extends CommonBaseEntity implements Serializable {

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

    @Column(name = "status", nullable = false)
    private Character status;

    public UserMst(UserDto user) {
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.contactNumber = user.getContactNumber();
        this.roleId = user.getRoleId();
        this.driverLicenseNo = user.getDriverLicenseNo();
        this.status = STATUS_ACTIVE;
    }
}
