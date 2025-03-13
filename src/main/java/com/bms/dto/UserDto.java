package com.bms.dto;

import com.bms.entity.UserMst;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Integer id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private Integer roleId;
    private String contactNumber;
    private String driverLicenseNo;
    private Character status;
    private String identifier;
    private MultipartFile drivingLicense;

    public UserDto(UserMst userMst) {
        this.id = userMst.getId();
        this.username = userMst.getUsername();
        this.password = userMst.getPassword();
        this.firstName = userMst.getFirstName();
        this.lastName = userMst.getLastName();
        this.email = userMst.getEmail();
        this.contactNumber = userMst.getContactNumber();
        this.driverLicenseNo = userMst.getDriverLicenseNo();
    }
}
