package com.bms.controller;

import com.bms.dto.AuthRequestDto;
import com.bms.service.AuthService;
import com.bms.util.BMSCheckedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.bms.controller.abst.Mappings.*;

@RestController
@RequestMapping(AUTH)
public class AuthController {

    private AuthService authService;

    @PostMapping(LOGIN_V1)
    public ResponseEntity<Object> login(@RequestBody AuthRequestDto authRequest) {
        return authService.authenticateUser(authRequest);
    }

    @PostMapping(REFRESH_V1)
    public ResponseEntity<Object> refresh(@RequestBody AuthRequestDto refreshTokenRequest) {
        return authService.refreshToken(refreshTokenRequest);
    }

    @PostMapping(SEND_PASSWORD_RESET_MAIL_V1)
    public ResponseEntity<Object> sendPasswordResetMail(@RequestParam String email) throws BMSCheckedException {
        return authService.sendPasswordResetMail(email);
    }

    @Autowired
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }
}

