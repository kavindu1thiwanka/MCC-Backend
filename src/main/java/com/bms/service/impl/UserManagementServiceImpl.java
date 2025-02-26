package com.bms.service.impl;

import com.bms.config.JwtUtil;
import com.bms.dto.AddressDto;
import com.bms.dto.UserDto;
import com.bms.entity.AddressMst;
import com.bms.entity.CommonEmailMst;
import com.bms.entity.CommonEmailTemplate;
import com.bms.entity.UserMst;
import com.bms.repository.AddressMstRepository;
import com.bms.repository.CommonEmailMstRepository;
import com.bms.repository.CommonEmailTemplateRepository;
import com.bms.repository.UserMstRepository;
import com.bms.service.UserManagementService;
import com.bms.util.BMSCheckedException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import java.util.*;

import static com.bms.util.CommonConstants.*;
import static com.bms.util.ExceptionMessages.*;

@Service
@Transactional
public class UserManagementServiceImpl implements UserManagementService, UserDetailsService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private UserMstRepository userMstRepository;
    private CommonEmailMstRepository commonEmailMstRepository;
    private CommonEmailTemplateRepository commonEmailTemplateRepository;
    private AddressMstRepository addressMstRepository;
    private JwtUtil jwtUtil;

    @Value(CONFIRM_USER_EMAIL_URL)
    private String confirmUserEmailUrl;
    @Value(LOGIN_URL)
    private String loginUrl;

    /**
     * This method is used to register users (Customers & Drivers)
     *
     * @param user user details
     * @return ResponseEntity
     */
    @Override
    public ResponseEntity<Object> registerUser(UserDto user) throws BMSCheckedException {

        validateUserCreate(user);

        UserMst userMst = new UserMst(user);

        if (userMst.getRoleId().equals(ROLE_ID_DRIVER) && (userMst.getDriverLicenseNo() == null || userMst.getDriverLicenseNo().isEmpty())) {
            throw new BMSCheckedException(DRIVER_LICENSE_NO_CANNOT_BE_EMPTY);
        }

        userMst.setPassword(passwordEncoder.encode(user.getPassword()));
        userMstRepository.save(userMst);

        sendConfirmationEmail(userMst);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * This method is used to send confirmation email to user
     *
     * @param userMst user details
     */
    private void sendConfirmationEmail(UserMst userMst) throws BMSCheckedException {

        Optional<CommonEmailTemplate> templateOpt = commonEmailTemplateRepository.findById(EMAIL_TEMPLATE_CONFIGURE_USER);

        if (templateOpt.isEmpty()) {
            throw new BMSCheckedException(EMAIL_TEMPLATE_NOT_FOUND);
        }

        CommonEmailTemplate emailTemplate = templateOpt.get();

        Document html = Jsoup.parse(emailTemplate.getTemplateData(), CHARACTER_TYPE);

        Element emailSendToElement = html.body().getElementById(PARAM_EMAIL_SEND_TO);
        emailSendToElement.html(userMst.getFirstName().concat(EMPTY_SPACE_STRING).concat(userMst.getLastName() == null ? EMPTY_STRING : userMst.getLastName()));

        Element configurationUrlElement = html.body().getElementById(PARAM_CONFIGURATION_URL);
        configurationUrlElement.attr(HREF_ATTR, confirmUserEmailUrl.replace(PARAM_UUID, userMst.getUuid()));

        CommonEmailMst commonEmailMst = new CommonEmailMst(userMst.getEmail(), emailTemplate.getSubject(), html.html());
        commonEmailMstRepository.save(commonEmailMst);
    }

    /**
     * This method is used to update user details
     *
     * @param userDetails user details
     * @return HttpStatus 200
     */
    @Override
    public ResponseEntity<Object> updateUser(UserDto userDetails) throws BMSCheckedException {

        if (userDetails.getId() == null) {
            throw new BMSCheckedException(USER_ID_CANNOT_BE_EMPTY);
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
    public ResponseEntity<Object> activateUser(Integer userId) throws BMSCheckedException {

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
    public ResponseEntity<Object> inactivateUser(Integer userId) throws BMSCheckedException {

        changeUserStatus(new ArrayList<>(userId), STATUS_INACTIVE);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This method is used to delete user
     *
     * @param userId user id
     * @return HttpStatus 200
     */
    @Override
    public ResponseEntity<Object> deleteUser(Integer userId) throws BMSCheckedException {

        changeUserStatus(new ArrayList<>(List.of(userId)), STATUS_DELETE);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This method is used to change user's status
     */
    private void changeUserStatus(List<Integer> userIdList, Character status) throws BMSCheckedException {

        if (userIdList == null || userIdList.isEmpty()) {
            throw new BMSCheckedException(USER_IDS_CANNOT_BE_EMPTY);
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
     * This method is used to confirm user email
     *
     * @param uuid uuid
     * @return HttpStatus
     */
    @Override
    public ResponseEntity<Object> confirmUserEmail(String uuid) throws BMSCheckedException {

        Optional<UserMst> userOpt = userMstRepository.findUserByUuid(uuid);

        if (userOpt.isEmpty()) {
            throw new BMSCheckedException(USER_NOT_FOUND);
        }

        UserMst user = userOpt.get();

        if (user.getStatus().equals(STATUS_ACTIVE)) {
            return new ResponseEntity<>(HttpStatus.ALREADY_REPORTED);
        }

        user.setStatus(STATUS_ACTIVE);
        userMstRepository.save(user);

        sendRegistrationSuccessEmail(user);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This method is used to send registration success email
     */
    private void sendRegistrationSuccessEmail(UserMst user) throws BMSCheckedException {
        Optional<CommonEmailTemplate> templateOpt = commonEmailTemplateRepository.findById(EMAIL_TEMPLATE_REGISTRATION_SUCCESS);

        if (templateOpt.isEmpty()) {
            throw new BMSCheckedException(EMAIL_TEMPLATE_NOT_FOUND);
        }

        CommonEmailTemplate emailTemplate = templateOpt.get();

        Document html = Jsoup.parse(emailTemplate.getTemplateData(), CHARACTER_TYPE);

        Element emailSendToElement = html.body().getElementById(PARAM_EMAIL_SEND_TO);
        emailSendToElement.html(user.getFirstName().concat(EMPTY_SPACE_STRING).concat(user.getLastName() == null ? EMPTY_STRING : user.getLastName()));

        Element loginUrlElement = html.body().getElementById(PARAM_LOGIN_URL);
        loginUrlElement.attr(HREF_ATTR, loginUrl);

        Element usernameElement = html.body().getElementById(PARAM_USERNAME);
        usernameElement.html(user.getUsername());

        CommonEmailMst commonEmailMst = new CommonEmailMst(user.getEmail(), emailTemplate.getSubject(), html.html());
        commonEmailMstRepository.save(commonEmailMst);
    }

    /**
     * This method is used to get user address
     */
    @Override
    public ResponseEntity<Object> getUserAddress() {

        UserMst user = (UserMst) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<>(addressMstRepository.getAddressMstByUserName(user.getUsername()), HttpStatus.OK);
    }

    /**
     * This method is used to update current logged-in user's address
     *
     * @param address updated address details
     * @return HttpStatus
     */
    @Override
    public ResponseEntity<Object> updateUserAddress(AddressDto address) {

        UserMst user = (UserMst) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        AddressMst existingAddress = addressMstRepository.getAddressMstByUserName(user.getUsername());

        if (existingAddress == null) {
            existingAddress = AddressMst.builder()
                    .userId(user.getId())
                    .addressLine1(address.getAddressLine1())
                    .addressLine2(address.getAddressLine2())
                    .city(address.getCity())
                    .state(address.getState())
                    .country(address.getCountry())
                    .postalCode(address.getPostalCode())
                    .build();
        } else {
            existingAddress.copyAddressDetails(address);
        }
        addressMstRepository.save(existingAddress);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> resetPassword(Map<String, Object> requestBody) throws BMSCheckedException {
        String extractedUsername = jwtUtil.extractUsername((String) requestBody.get("token"));

        if (extractedUsername == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Optional<UserMst> userOpt = userMstRepository.findByUsername(extractedUsername);

        if (userOpt.isEmpty() || !userOpt.get().getStatus().equals(STATUS_ACTIVE)) {
            throw new BMSCheckedException(USER_NOT_FOUND);
        }

        UserMst user = userOpt.get();

        String newPassword = (String) requestBody.get("newPassword");

        if (newPassword == null || newPassword.isEmpty()) {
            throw new BMSCheckedException(NEW_PASSWORD_CANNOT_BE_EMPTY);
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BMSCheckedException(NEW_PASSWORD_SAME_AS_OLD_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userMstRepository.save(user);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This method is used to retrieve logged-in user details
     * 
     */
    @Override
    public ResponseEntity<Object> getLoggedInUserDetails() {

        UserMst user = (UserMst) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<UserMst> userOpt = userMstRepository.findById(user.getId());

        if (userOpt.isEmpty()) {
            throw new RuntimeException(USER_NOT_FOUND);
        }

        return new ResponseEntity<>(userOpt.get(), HttpStatus.OK);
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
    private void validateUserCreate(UserDto user) throws BMSCheckedException {

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new BMSCheckedException(USER_EMAIL_CANNOT_BE_EMPTY);
        } else {
            userMstRepository.findByEmailAndStatusNot(user.getEmail(), STATUS_DELETE).ifPresent(userMst -> {
                throw new RuntimeException(USER_ALREADY_EXISTS);
            });
        }

        if (user.getFirstName() == null || user.getFirstName().isEmpty()) {
            throw new BMSCheckedException(FIRST_NAME_CANNOT_BE_EMPTY);
        }

        if (user.getLastName() == null || user.getLastName().isEmpty()) {
            throw new BMSCheckedException(LAST_NAME_CANNOT_BE_EMPTY);
        }

        if (user.getContactNumber() == null || user.getContactNumber().isEmpty()) {
            throw new BMSCheckedException(USER_CONTACT_NUMBER_CANNOT_BE_EMPTY);
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new BMSCheckedException(USER_PASSWORD_CANNOT_BE_EMPTY);
        }

        if (user.getIdentifier() == null) {
            throw new BMSCheckedException(IDENTIFIER_NOT_FOUND);
        } else if (!List.of(IDENTIFIER_ROLE_CUSTOMER, IDENTIFIER_ROLE_DRIVER).contains(user.getIdentifier())) {
            throw new BMSCheckedException(INVALID_IDENTIFIER);
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

    @Autowired
    public void setCommonEmailMstRepository(CommonEmailMstRepository commonEmailMstRepository) {
        this.commonEmailMstRepository = commonEmailMstRepository;
    }

    @Autowired
    public void setCommonEmailTemplateRepository(CommonEmailTemplateRepository commonEmailTemplateRepository) {
        this.commonEmailTemplateRepository = commonEmailTemplateRepository;
    }

    @Autowired
    public void setAddressMstRepository(AddressMstRepository addressMstRepository) {
        this.addressMstRepository = addressMstRepository;
    }

    @Autowired
    public void setJwtUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
}
