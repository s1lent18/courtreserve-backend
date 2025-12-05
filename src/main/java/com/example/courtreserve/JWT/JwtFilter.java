package com.example.courtreserve.JWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // List of paths to bypass JWT filter
        return path.startsWith("/swagger-ui") ||
                path.equals("/swagger-ui.html") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/webjars/") ||
                path.equals("/openapi.yaml") ||
                path.startsWith("/user/login") ||
                path.startsWith("/user/register") ||
                path.startsWith("/vendor/login") ||
                path.startsWith("/vendor/register");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");
        String jwt = null;
        String username = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
        }

        String path = request.getRequestURI();
        System.out.println("Request Path: " + path);

        System.out.println("JwtFilter: Username from token = " + username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(jwt)) {
                List<String> roles = jwtUtil.extractRoles(jwt);

                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                System.out.println("✅ Authenticated user: " + auth.getName());
                System.out.println("✅ Authorities: " + auth.getAuthorities());
            }
        }
        chain.doFilter(request, response);
    }
}