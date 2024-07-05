package com.dperez.CarRegistry.filter;

import com.dperez.CarRegistry.service.UserService;
import com.dperez.CarRegistry.service.impl.JwtService;
import com.dperez.CarRegistry.service.impl.UserServiceImpl;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserServiceImpl userService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("No Authorization header or header does not start with Bearer");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7); // Remove "Bearer " prefix
        log.info("JWT -> {}", jwt);

        try {
            userEmail = jwtService.extractUserName(jwt);
            log.info("Extracted user email -> {}", userEmail);
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userEmail);
                log.info("Loaded user details -> {}", userDetails);
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    log.info("Token is valid");
                    log.info("User -> {}", userDetails);
                    log.info("Authorities -> {}", userDetails.getAuthorities());
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    log.warn("Token is not valid");
                }
            }
        } catch (Exception e) {
            log.error("Error validating token", e);
        }

        filterChain.doFilter(request, response);
    }
//        ***************************************************************************

//        if (StringUtils.isEmpty(authHeader)){
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        jwt = authHeader.substring((7)); // Bearer xxxx
//        log.info("JWT -> {}", jwt.toString());
//
//        userEmail = jwtService.extractUserName(jwt);
//        if(!StringUtils.isEmpty(userEmail) && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userEmail);
//            if(jwtService.isTokenValid(jwt, userDetails)){
//                log.info("User -> {}", userDetails);
//                log.info("Authorities -> {}", userDetails.getAuthorities());
//                SecurityContext context = SecurityContextHolder.createEmptyContext();
//                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                        userDetails, null, userDetails.getAuthorities());
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                context.setAuthentication(authToken);
//                SecurityContextHolder.setContext(context);
//            }
//        }
//        filterChain.doFilter(request, response);

    }




