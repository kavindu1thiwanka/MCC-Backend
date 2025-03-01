package com.bms.config;

import com.bms.entity.UserMst;
import com.bms.repository.PrivilegeMstRepository;
import com.bms.repository.UserMstRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.bms.util.CommonConstants.*;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtUtil jwtUtil;
    private UserMstRepository userMstRepository;
    private PrivilegeMstRepository privilegeMstRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if (request.getRequestURI().startsWith("/auth/") || request.getRequestURI().equals("/user/v1/register")
                || request.getRequestURI().equals("/user/v1/confirm") || request.getRequestURI().equals("/vehicle/v1/get_vehicle_list")
                || request.getRequestURI().equals("/vehicle/v1/get_vehicle_total_cost")) {
            chain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"status\":401,\"message\":\"Authorization header missing or invalid\"}");
            return;
        }

        jwt = authorizationHeader.substring(7);

        try {
            username = jwtUtil.extractUsername(jwt);
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token is invalid or expired");
            return;
        }

        if (username != null && jwtUtil.validateToken(jwt, username)) {

            Optional<UserMst> userOpt = userMstRepository.findByUsername(username);

            if (userOpt.isEmpty()) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "User not found");
                return;
            }

            UserMst user = userOpt.get();

            Set<String> authCodes = privilegeMstRepository.findPrivilegeIdByRoleId(user.getRoleId());

            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user,
                    null, grantAuthorityCodes(authCodes, user.getRoleId())));

        } else {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token is invalid or expired");
            return;
        }

        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            if (response.getStatus() == HttpServletResponse.SC_OK) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            sendErrorResponse(response, response.getStatus(), e.getCause().getMessage());
        }
    }

    /**
     * This method is used to generate a list of GrantedAuthority objects from
     * the auth codes that the user has.
     */
    private Set<GrantedAuthority> grantAuthorityCodes(Set<String> authCodes, Integer roleId) {
        Set<GrantedAuthority> authorityList = new HashSet<>();
        authCodes.forEach((authority) -> {
            authorityList.add(new SimpleGrantedAuthority(authority));
        });

        if (roleId.equals(ROLE_ID_ADMIN)) {
            authorityList.add(new SimpleGrantedAuthority(ROLE_ADMIN_WITH_ROLE_PREFIX));
        }

        if (roleId.equals(ROLE_ID_CUSTOMER)) {
            authorityList.add(new SimpleGrantedAuthority(ROLE_CUSTOMER_WITH_ROLE_PREFIX));
        }

        if (roleId.equals(ROLE_ID_DRIVER)) {
            authorityList.add(new SimpleGrantedAuthority(ROLE_DRIVER_WITH_ROLE_PREFIX));
        }

        return authorityList;
    }

    /**
     * Helper method to send JSON error responses with correct status codes.
     */
    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"status\":" + status + ",\"message\":\"" + message + "\"}");
    }

    @Autowired
    public void setJwtUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Autowired
    public void setUserMstRepository(UserMstRepository userMstRepository) {
        this.userMstRepository = userMstRepository;
    }

    @Autowired
    public void setPrivilegeMstRepository(PrivilegeMstRepository privilegeMstRepository) {
        this.privilegeMstRepository = privilegeMstRepository;
    }

}
