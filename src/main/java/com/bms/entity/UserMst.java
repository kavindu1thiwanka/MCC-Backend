package com.bms.entity;

import com.bms.dto.UserDto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.bms.util.CommonConstant.STATUS_ACTIVE;

@Entity
@Data
@NoArgsConstructor
@Table(name = "user_mst")
public class UserMst {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "status")
    private Character status;

    @Transient
    private Set<String> roleList = new HashSet<>();
    @Transient
    private Set<Integer> roleIdList = new HashSet<>();

    public UserMst(UserDto user) {
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.contactNumber = user.getContactNumber();
        this.status = STATUS_ACTIVE;
    }
}
