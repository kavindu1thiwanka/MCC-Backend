package com.bms.service.impl;

import com.bms.dto.UserDto;
import com.bms.entity.UserMst;
import com.bms.repository.UserMstRepository;
import com.bms.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.bms.util.CommonConstants.*;
import static com.bms.util.ExceptionMessages.*;

@Service
@Transactional
public class UserManagementServiceImpl implements UserManagementService, UserDetailsService {

    private UserMstRepository userMstRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * This method is used to create user
     *
     * @param user user details
     * @return ResponseEntity
     */
    @Override
    public ResponseEntity<Object> createUser(UserDto user) {

        validateUserCreate(user);

        UserMst userMst = new UserMst(user);
        userMst.setPassword(passwordEncoder.encode(user.getPassword()));
        userMstRepository.save(userMst);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * This method is used to update user details
     *
     * @param userDetails user details
     * @return HttpStatus 200
     */
    @Override
    public ResponseEntity<Object> updateUser(UserDto userDetails) {

        if (userDetails.getId() == null) {
            throw new IllegalArgumentException(USER_ID_CANNOT_BE_EMPTY);
        }

        UserMst existingUser = getExistingUser(userDetails.getId());
        existingUser.updateUserDetails(userDetails);
        existingUser.setPassword(userDetails.getPassword() == null
                ? existingUser.getPassword() : passwordEncoder.encode(userDetails.getPassword()));
        setUsersUpdatedMetaData(existingUser);

        userMstRepository.save(existingUser);

        return new ResponseEntity<>(HttpStatus.OK);

    }

    /**
     * This method is used to activate user
     *
     * @param userId user id
     * @return HttpStatus 200
     */
    @Override
    public ResponseEntity<Object> activateUser(Integer userId) {

        changeUserStatus(new ArrayList<>(List.of(userId)), STATUS_ACTIVE);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This method is used to inactivate user
     *
     * @param userId user id
     * @return HttpStatus 200
     */
    @Override
    public ResponseEntity<Object> inactivateUser(Integer userId) {

        changeUserStatus(new ArrayList<>(List.of(userId)), STATUS_INACTIVE);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This method is used to delete user
     *
     * @param userId user id
     * @return HttpStatus 200
     */
    @Override
    public ResponseEntity<Object> deleteUser(Integer userId) {

        changeUserStatus(new ArrayList<>(List.of(userId)), STATUS_DELETE);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This method is used to change user's status
     */
    private void changeUserStatus(List<Integer> userIdList, Character status) {

        if (userIdList == null || userIdList.isEmpty()) {
            throw new IllegalArgumentException(USER_IDS_CANNOT_BE_EMPTY);
        }

        List<UserMst> updatedUserList = new ArrayList<>();

        for (Integer userId : userIdList) {
            UserMst existingUser = getExistingUser(userId);
            existingUser.setStatus(status);
            setUsersUpdatedMetaData(existingUser);
            updatedUserList.add(existingUser);
        }

        userMstRepository.saveAll(updatedUserList);
    }

    /**
     * This method is used to retrieve user details related to provided user id
     *
     * @param userId user id
     * @return HttpStatus 200 with user details
     */
    @Override
    public ResponseEntity<Object> getUserDetails(Integer userId) {
        return new ResponseEntity<>(getExistingUser(userId), HttpStatus.OK);
    }

    /**
     * This method is used to retrieve user details related to provided user id
     */
    private UserMst getExistingUser(Integer userId) {

        Optional<UserMst> existingUserOpt = userMstRepository.findById(userId);

        if (existingUserOpt.isEmpty()) {
            throw new RuntimeException(USER_NOT_FOUND);
        }

        return existingUserOpt.get();
    }

    /**
     * This method is used to set the updated metadata like updatedBy and updatedOn
     */
    private void setUsersUpdatedMetaData(UserMst existingUser) {
        UserMst user = (UserMst) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        existingUser.setUpdateBy(user.getEmail());
        existingUser.setUpdateOn(new Date());
    }

    /**
     * This method is used to validate user details
     *
     * @param user user details
     */
    private void validateUserCreate(UserDto user) {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new IllegalArgumentException(USERNAME_CANNOT_BE_EMPTY);
        } else {
            userMstRepository.findByUsernameAndStatusNot(user.getUsername(), STATUS_DELETE).ifPresent(userMst -> {
                throw new IllegalArgumentException(USERNAME_ALREADY_EXISTS);
            });
        }

        if (user.getFirstName() == null || user.getFirstName().isEmpty()) {
            throw new IllegalArgumentException(FIRST_NAME_CANNOT_BE_EMPTY);
        }

        if (user.getLastName() == null || user.getLastName().isEmpty()) {
            throw new IllegalArgumentException(LAST_NAME_CANNOT_BE_EMPTY);
        }

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException(USER_EMAIL_CANNOT_BE_EMPTY);
        }

        if (user.getContactNumber() == null || user.getContactNumber().isEmpty()) {
            throw new IllegalArgumentException(USER_CONTACT_NUMBER_CANNOT_BE_EMPTY);
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException(USER_PASSWORD_CANNOT_BE_EMPTY);
        }

        if (user.getRoleId() == null) {
            throw new IllegalArgumentException(ROLE_ID_CANNOT_BE_EMPTY);
        }

        if (user.getRoleId().equals(ROLE_ID_DRIVER) && (user.getDriverLicenseNo() == null || user.getDriverLicenseNo().isEmpty())) {
            throw new IllegalArgumentException(DRIVER_LICENSE_NO_CANNOT_BE_EMPTY);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserMst user = userMstRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));

        String role = "";

        if (user.getRoleId().equals(ROLE_ID_ADMIN)) {
            role = ROLE_ADMIN;
        }

        if (user.getRoleId().equals(ROLE_ID_CUSTOMER)) {
            role = ROLE_CUSTOMER;
        }

        if (user.getRoleId().equals(ROLE_ID_DRIVER)) {
            role = ROLE_DRIVER;
        }

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(role)
                .build();
    }

    @Autowired
    public void setUserMstRepository(UserMstRepository userMstRepository) {
        this.userMstRepository = userMstRepository;
    }
}
