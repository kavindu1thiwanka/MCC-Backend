package com.bms.config;

import com.bms.entity.UserMst;
import com.bms.repository.PrivilegeMstRepository;
import com.bms.repository.RoleMstRepository;
import com.bms.repository.UserMstRepository;
import com.bms.repository.UserWiseRolesRepository;
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

import static com.bms.util.CommonConstant.*;
import static com.bms.util.CommonConstant.ROLE_STUDENT;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtUtil jwtUtil;
    private UserMstRepository userMstRepository;
    private PrivilegeMstRepository privilegeMstRepository;
    private UserWiseRolesRepository userWiseRolesRepository;
    private RoleMstRepository roleMstRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if (request.getRequestURI().startsWith("/auth/")) {
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
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token");
            return;
        }

        if (username != null && jwtUtil.validateToken(jwt, username)) {

            Optional<UserMst> userOpt = userMstRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("User not found");
                return;
            }

            UserMst user = userOpt.get();

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

        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token is invalid or expired");
            return;
        }

        chain.doFilter(request, response);
    }

    public Set<GrantedAuthority> grantAuthorityCodes(Set<String> authCodes) {
        Set<GrantedAuthority> authorityList = new HashSet<>();
        authCodes.forEach((authority) -> {
            authorityList.add(new SimpleGrantedAuthority(authority));
        });
        return authorityList;
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

    @Autowired
    public void setUserWiseRolesRepository(UserWiseRolesRepository userWiseRolesRepository) {
        this.userWiseRolesRepository = userWiseRolesRepository;
    }

    @Autowired
    public void setRoleMstRepository(RoleMstRepository roleMstRepository) {
        this.roleMstRepository = roleMstRepository;
    }
}
