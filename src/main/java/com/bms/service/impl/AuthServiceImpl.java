package com.bms.service.impl;

import com.bms.config.JwtUtil;
import com.bms.dto.AuthRequestDto;
import com.bms.entity.UserMst;
import com.bms.repository.PrivilegeMstRepository;
import com.bms.repository.RoleMstRepository;
import com.bms.repository.UserMstRepository;
import com.bms.repository.UserWiseRolesRepository;
import com.bms.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.bms.util.CommonConstant.*;

@Service
public class AuthServiceImpl implements AuthService {

    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;

    private UserMstRepository userMstRepository;
    private PrivilegeMstRepository privilegeMstRepository;
    private UserWiseRolesRepository userWiseRolesRepository;
    private RoleMstRepository roleMstRepository;

    @Override
    public ResponseEntity<Object> authenticateUser(AuthRequestDto authRequest) {
        Authentication authentication;
        try {

            Optional<UserMst> userOptional = userMstRepository.findByUsername(authRequest.getUsername());
            if (userOptional.isEmpty()) {
                return new ResponseEntity<>("User not found", HttpStatus.UNAUTHORIZED);
            }

            UserMst user = userOptional.get();

            if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
                return new ResponseEntity<>("Invalid password", HttpStatus.UNAUTHORIZED);
            }

            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            user.setRoleIdList(userWiseRolesRepository.getRoleIdListByUserId(user.getId()));
            Set<String> authCodes = privilegeMstRepository.findPrivilegeIdByRoleIdList(user.getRoleIdList());
            Set<Integer> mainRoleIdList = roleMstRepository.getMainRoleIdList(user.getRoleIdList());

            if (mainRoleIdList.contains(ROLE_ID_ADMIN)) {
                user.getRoleList().add(ROLE_ADMIN);
            }

            if (mainRoleIdList.contains(ROLE_ID_TEACHER)) {
                user.getRoleList().add(ROLE_TEACHER);
            }

            if (mainRoleIdList.contains(ROLE_ID_STUDENT)) {
                user.getRoleList().add(ROLE_STUDENT);
            }

            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user,
                    null, grantAuthorityCodes(authCodes)));

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }

        // Generate JWT if authentication is successful
        return new ResponseEntity<>(new AuthRequestDto(jwtUtil.generateToken(authentication.getName())), HttpStatus.OK);
    }

    public Set<GrantedAuthority> grantAuthorityCodes(Set<String> authCodes) {
        Set<GrantedAuthority> authorityList = new HashSet<>();
        authCodes.forEach((authority) -> {
            authorityList.add(new SimpleGrantedAuthority(authority));
        });
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
    public void setUserWiseRolesRepository(UserWiseRolesRepository userWiseRolesRepository) {
        this.userWiseRolesRepository = userWiseRolesRepository;
    }

    @Autowired
    public void setRoleMstRepository(RoleMstRepository roleMstRepository) {
        this.roleMstRepository = roleMstRepository;
    }
}
