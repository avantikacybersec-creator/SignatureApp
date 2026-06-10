package com.signature.signatureapp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("REQUEST URI = " + request.getRequestURI());

        String header = request.getHeader("Authorization");

        System.out.println("HEADER = " + header);

        if(header != null &&
                header.startsWith("Bearer ")){

            String token = header.substring(7);

            System.out.println("TOKEN RECEIVED");

            boolean valid = jwtService.validateToken(token);

            System.out.println("TOKEN VALID = " + valid);

            if(valid){

                System.out.println("TOKEN VALID");

                String email =
                        jwtService.extractEmail(token);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                AuthorityUtils.NO_AUTHORITIES
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(auth);
            }
        }

        filterChain.doFilter(request,response);
    }
}