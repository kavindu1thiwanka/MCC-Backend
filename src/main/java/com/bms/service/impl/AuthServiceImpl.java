package com.bms.service.impl;

import com.bms.config.JwtUtil;
import com.bms.dto.AuthRequestDto;
import com.bms.entity.CommonEmailMst;
import com.bms.entity.CommonEmailTemplate;
import com.bms.entity.UserMst;
import com.bms.repository.CommonEmailMstRepository;
import com.bms.repository.CommonEmailTemplateRepository;
import com.bms.repository.PrivilegeMstRepository;
import com.bms.repository.UserMstRepository;
import com.bms.service.AuthService;
import com.bms.util.BMSCheckedException;
import com.bms.util.ExceptionMessages;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.bms.util.CommonConstants.*;
import static com.bms.util.ExceptionMessages.EMAIL_TEMPLATE_NOT_FOUND;

@Service
public class AuthServiceImpl implements AuthService {

    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private UserMstRepository userMstRepository;
    private PrivilegeMstRepository privilegeMstRepository;
    private CommonEmailTemplateRepository commonEmailTemplateRepository;
    private CommonEmailMstRepository commonEmailMstRepository;

    @Value(PWD_RESET_URL)
    private String pwdResetUrl;

    @Override
    public ResponseEntity<Object> authenticateUser(AuthRequestDto authRequest) {
        try {
            Optional<UserMst> userOptional = userMstRepository.findByUsername(authRequest.getUsername());
            if (userOptional.isEmpty()) {
                return new ResponseEntity<>(ExceptionMessages.USER_NOT_FOUND, HttpStatus.UNAUTHORIZED);
            }

            UserMst user = userOptional.get();

            if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
                return new ResponseEntity<>(ExceptionMessages.INVALID_PASSWORD, HttpStatus.UNAUTHORIZED);
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            Set<String> authCodes = privilegeMstRepository.findPrivilegeIdByRoleId(user.getRoleId());

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            user, null, grantAuthorityCodes(authCodes)));

            AuthRequestDto response = new AuthRequestDto(
                    jwtUtil.generateAccessToken(authentication.getName()),
                    jwtUtil.generateRefreshToken(authentication.getName()),
                    user);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<Object> refreshToken(AuthRequestDto request) {
        try {
            String refreshToken = request.getRefreshToken();
            String username = jwtUtil.extractUsername(refreshToken);

            if (jwtUtil.validateToken(refreshToken, username)) {
                String newAccessToken = jwtUtil.generateAccessToken(username);
                return new ResponseEntity<>(new AuthRequestDto(newAccessToken, refreshToken), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Invalid refresh token.", HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> sendPasswordResetMail(String email) throws BMSCheckedException {

        Optional<UserMst> userOpt = userMstRepository.findByEmailAndStatusNot(email, STATUS_DELETE);

        if (userOpt.isEmpty()) {
            throw new BMSCheckedException(ExceptionMessages.USER_NOT_FOUND);
        }

        UserMst user = userOpt.get();

        String resetToken = jwtUtil.generatePasswordResetToken(user.getUsername());

        sendPasswordResetEmail(user, resetToken);
        
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void sendPasswordResetEmail(UserMst user, String resetToken) throws BMSCheckedException {
        Optional<CommonEmailTemplate> templateOpt = commonEmailTemplateRepository.findById(EMAIL_TEMPLATE_PWD_RESET);

        if (templateOpt.isEmpty()) {
            throw new BMSCheckedException(EMAIL_TEMPLATE_NOT_FOUND);
        }

        CommonEmailTemplate emailTemplate = templateOpt.get();

        Document html = Jsoup.parse(emailTemplate.getTemplateData(), CHARACTER_TYPE);

        Element emailSendToElement = html.body().getElementById(PARAM_EMAIL_SEND_TO);
        emailSendToElement.html(user.getFirstName().concat(EMPTY_SPACE_STRING).concat(user.getLastName() == null ? EMPTY_STRING : user.getLastName()));

        Element loginUrlElement = html.body().getElementById(PARAM_PASSWORD_RESET_URL);
        loginUrlElement.attr(HREF_ATTR, pwdResetUrl.replace(PARAM_PWD_RESET_TOKEN, resetToken));

        CommonEmailMst commonEmailMst = new CommonEmailMst(user.getEmail(), emailTemplate.getSubject(), html.html());
        commonEmailMstRepository.save(commonEmailMst);
    }

    public Set<GrantedAuthority> grantAuthorityCodes(Set<String> authCodes) {
        Set<GrantedAuthority> authorityList = new HashSet<>();
        authCodes.forEach(auth -> authorityList.add(new SimpleGrantedAuthority(auth)));
        return authorityList;
    }

    @Autowired
    public void setUserMstRepository(UserMstRepository userMstRepository) {
        this.userMstRepository = userMstRepository;
    }

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    public void setJwtUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setPrivilegeMstRepository(PrivilegeMstRepository privilegeMstRepository) {
        this.privilegeMstRepository = privilegeMstRepository;
    }

    @Autowired
    public void setCommonEmailTemplateRepository(CommonEmailTemplateRepository commonEmailTemplateRepository) {
        this.commonEmailTemplateRepository = commonEmailTemplateRepository;
    }

    @Autowired
    public void setCommonEmailMstRepository(CommonEmailMstRepository commonEmailMstRepository) {
        this.commonEmailMstRepository = commonEmailMstRepository;
    }
}
