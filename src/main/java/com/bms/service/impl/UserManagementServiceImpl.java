package com.bms.service.impl;

import com.bms.config.JwtUtil;
import com.bms.dto.AddressDto;
import com.bms.dto.UserDto;
import com.bms.entity.AddressMst;
import com.bms.entity.CommonEmailMst;
import com.bms.entity.CommonEmailTemplate;
import com.bms.entity.UserMst;
import com.bms.exception.BusinessException;
import com.bms.repository.AddressMstRepository;
import com.bms.repository.CommonEmailMstRepository;
import com.bms.repository.CommonEmailTemplateRepository;
import com.bms.repository.UserMstRepository;
import com.bms.service.FileStorageService;
import com.bms.service.UserManagementService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.bms.util.CommonConstants.*;
import static com.bms.util.ExceptionMessages.*;

@Service
public class UserManagementServiceImpl implements UserManagementService, UserDetailsService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private UserMstRepository userMstRepository;
    private CommonEmailMstRepository commonEmailMstRepository;
    private CommonEmailTemplateRepository commonEmailTemplateRepository;
    private AddressMstRepository addressMstRepository;
    private JwtUtil jwtUtil;
    private FileStorageService fileStorageService;

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
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> registerUser(UserDto user) throws BusinessException {

        validateUserCreate(user);

        UserMst userMst = new UserMst(user);

        userMst.setPassword(passwordEncoder.encode(user.getPassword()));
        userMstRepository.save(userMst);

        sendConfirmationEmail(userMst);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * This method is used to create users through admin dashboard
     * the difference between this method and registerUser is that this method will not send confirmation email
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> createUser(UserDto user) throws BusinessException, IOException {

        validateUserCreate(user);
        UserMst userMst = new UserMst(user);
        userMst.setStatus(STATUS_ACTIVE);
        userMst.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getDrivingLicense() != null) {
            userMst.setDriverLicenseUrl(fileStorageService.uploadDriverLicense(user.getDrivingLicense(), user.getDriverLicenseNo(), null));
        }
        userMstRepository.save(userMst);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * This method is used to send confirmation email to user
     *
     * @param userMst user details
     */
    private void sendConfirmationEmail(UserMst userMst) throws BusinessException {

        Optional<CommonEmailTemplate> templateOpt = commonEmailTemplateRepository.findById(EMAIL_TEMPLATE_CONFIGURE_USER);

        if (templateOpt.isEmpty()) {
            throw new BusinessException(EMAIL_TEMPLATE_NOT_FOUND);
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
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> updateUser(UserDto userDetails) throws BusinessException, IOException {

        if (userDetails.getId() == null) {
            throw new BusinessException(USER_ID_CANNOT_BE_EMPTY);
        }

        UserMst existingUser = getExistingUser(userDetails.getId());
        existingUser.updateUserDetails(userDetails);
        existingUser.setPassword((userDetails.getPassword() == null || userDetails.getPassword().isEmpty())
                ? existingUser.getPassword() : passwordEncoder.encode(userDetails.getPassword()));
        setUsersUpdatedMetaData(existingUser);

        if (userDetails.getDrivingLicense() != null) {
            existingUser.setDriverLicenseUrl(fileStorageService.uploadDriverLicense(userDetails.getDrivingLicense(), userDetails.getDriverLicenseNo(),
                    existingUser.getDriverLicenseUrl()));
        }

        userMstRepository.save(existingUser);

        UserMst user = (UserMst) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        existingUser.setLoggedInProfileUpdated(user.getUsername().equals(existingUser.getUsername()));

        return new ResponseEntity<>(existingUser, HttpStatus.OK);

    }

    /**
     * This method is used to change user status
     *
     * @param userId userId that needs to be changed the status
     * @param status Active, Inactive or Deleted
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> changeUserStatus(Integer userId, Character status) throws BusinessException {
        if (userId == null) {
            throw new BusinessException(USER_ID_CANNOT_BE_EMPTY);
        }

        if (status == null) {
            throw new BusinessException(USER_STATUS_CANNOT_BE_EMPTY);
        }

        UserMst existingUser = getExistingUser(userId);

        UserMst user = (UserMst) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user.getUsername().equals(existingUser.getUsername())) {
            throw new BusinessException(USER_STATUS_CHANGE_NOT_ALLOWED);
        }

        existingUser.setStatus(status);
        setUsersUpdatedMetaData(existingUser);

        userMstRepository.save(existingUser);
        return new ResponseEntity<>(HttpStatus.OK);
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
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> confirmUserEmail(String uuid) throws BusinessException {

        Optional<UserMst> userOpt = userMstRepository.findUserByUuid(uuid);

        if (userOpt.isEmpty()) {
            throw new BusinessException(USER_NOT_FOUND);
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
    private void sendRegistrationSuccessEmail(UserMst user) throws BusinessException {
        Optional<CommonEmailTemplate> templateOpt = commonEmailTemplateRepository.findById(EMAIL_TEMPLATE_REGISTRATION_SUCCESS);

        if (templateOpt.isEmpty()) {
            throw new BusinessException(EMAIL_TEMPLATE_NOT_FOUND);
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
    @Transactional(rollbackFor = Exception.class)
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

    /**
     * This method is used to reset password
     *
     * @param requestBody token and new password
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> resetPassword(Map<String, Object> requestBody) throws BusinessException {
        String extractedUsername = jwtUtil.extractUsername((String) requestBody.get("token"));

        if (extractedUsername == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Optional<UserMst> userOpt = userMstRepository.findByUsername(extractedUsername);

        if (userOpt.isEmpty() || !userOpt.get().getStatus().equals(STATUS_ACTIVE)) {
            throw new BusinessException(USER_NOT_FOUND);
        }

        UserMst user = userOpt.get();

        String newPassword = (String) requestBody.get("newPassword");

        if (newPassword == null || newPassword.isEmpty()) {
            throw new BusinessException(NEW_PASSWORD_CANNOT_BE_EMPTY);
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BusinessException(NEW_PASSWORD_SAME_AS_OLD_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userMstRepository.save(user);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This method is used to retrieve logged-in user details
     */
    @Override
    public ResponseEntity<Object> getLoggedInUserDetails() {

        UserMst user = (UserMst) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<UserMst> userOpt = userMstRepository.findById(user.getId());

        if (userOpt.isEmpty()) {
            throw new RuntimeException(USER_NOT_FOUND);
        }

        UserDto userDto = new UserDto(userOpt.get());

        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    /**
     * This method is used to retrieve all users
     */
    @Override
    public ResponseEntity<Object> getAllUsers() {
        return new ResponseEntity<>(userMstRepository.getAllUsers(), HttpStatus.OK);
    }

    /**
     * This method is used to retrieve all drivers
     */
    @Override
    public ResponseEntity<Object> getAllDrivers() {
        return new ResponseEntity<>(userMstRepository.getAllDrivers(), HttpStatus.OK);
    }

    /**
     * This method is used to retrieve all admins
     */
    @Override
    public ResponseEntity<Object> getAllAdmins() {
        return new ResponseEntity<>(userMstRepository.getAllAdmins(), HttpStatus.OK);
    }

    /**
     * This method is used to retrieve user details related to provided user id
     */
    private UserMst getExistingUser(Integer userId) {

        Optional<UserMst> existingUserOpt = userMstRepository.findById(userId);

        if (existingUserOpt.isEmpty()) {
            throw new BusinessException(USER_NOT_FOUND);
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
    private void validateUserCreate(UserDto user) throws BusinessException {

        if (user.getIdentifier() == null) {
            throw new BusinessException(IDENTIFIER_NOT_FOUND);
        } else if (!List.of(IDENTIFIER_ROLE_CUSTOMER, IDENTIFIER_ROLE_DRIVER, IDENTIFIER_ROLE_ADMIN).contains(user.getIdentifier())) {
            throw new BusinessException(INVALID_IDENTIFIER);
        }

        switch (user.getIdentifier()) {
            case IDENTIFIER_ROLE_CUSTOMER:
                user.setId(ROLE_ID_CUSTOMER);
                break;
            case IDENTIFIER_ROLE_DRIVER:
                user.setId(ROLE_ID_DRIVER);
                break;
            case IDENTIFIER_ROLE_ADMIN:
                user.setId(ROLE_ID_ADMIN);
                break;
        }

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new BusinessException(USER_EMAIL_CANNOT_BE_EMPTY);
        } else {
            userMstRepository.findByEmailAndRoleIdAndStatusNot(user.getEmail(), user.getRoleId(), STATUS_DELETE).ifPresent(userMst -> {
                throw new RuntimeException(USER_ALREADY_EXISTS);
            });
        }

        if (user.getFirstName() == null || user.getFirstName().isEmpty()) {
            throw new BusinessException(FIRST_NAME_CANNOT_BE_EMPTY);
        }

        if (user.getLastName() == null || user.getLastName().isEmpty()) {
            throw new BusinessException(LAST_NAME_CANNOT_BE_EMPTY);
        }

        if (user.getContactNumber() == null || user.getContactNumber().isEmpty()) {
            throw new BusinessException(USER_CONTACT_NUMBER_CANNOT_BE_EMPTY);
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new BusinessException(USER_PASSWORD_CANNOT_BE_EMPTY);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserMst user = userMstRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));

        String role = "";

        if (user.getRoleId().equals(ROLE_ID_ADMIN)) {
            role = ROLE_ADMIN_WITH_ROLE_PREFIX;
        }

        if (user.getRoleId().equals(ROLE_ID_CUSTOMER)) {
            role = ROLE_CUSTOMER_WITH_ROLE_PREFIX;
        }

        if (user.getRoleId().equals(ROLE_ID_DRIVER)) {
            role = ROLE_DRIVER_WITH_ROLE_PREFIX;
        }

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(role)
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

    @Autowired
    public void setFileStorageService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }
}
