package com.rainydaysengine.rainydays.config;

import com.rainydaysengine.rainydays.application.service.jwt.Jwt;
import com.rainydaysengine.rainydays.application.service.user.UserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private Jwt jwtService;

    @Autowired
    ApplicationContext context;

    /**
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJrcm94YXNAa2FybC5jb20iLCJpYXQiOjE3NjM1NjEzNzEsImV4cCI6MTc2MzU2NDk3MX0.HFagj7scu6R3OLXKkzOVIVCqM8R8dnFEwDoiCkPcBBc
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String identity = null;

        // We want to get only the JWT token and remove the "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Start with first index of JWT token
            token = authHeader.substring(7);
            identity = jwtService.extractUsername(token);
        }

        // If there is no current token/auth details
        if (identity != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = context.getBean(UserDetailsService.class).loadUserByUsername(identity);
            System.out.println("USER DETAILS: " + userDetails.getUsername());

            if (jwtService.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
