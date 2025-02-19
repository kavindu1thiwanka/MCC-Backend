package com.bms.service.impl;

import com.bms.config.JwtUtil;
import com.bms.dto.AuthRequestDto;
import com.bms.entity.UserMst;
import com.bms.repository.PrivilegeMstRepository;
import com.bms.repository.UserMstRepository;
import com.bms.service.AuthService;
import com.bms.util.ExceptionMessages;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private UserMstRepository userMstRepository;
    private PrivilegeMstRepository privilegeMstRepository;

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
}
