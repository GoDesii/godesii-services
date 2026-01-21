package com.godesii.godesii_services.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.godesii.godesii_services.common.APIError;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public final class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final UserDetailsService userDetailsService;
    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(JwtProvider jwtProvider, UserDetailsService userDetailsService) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            LOGGER.debug("Authorization header not present");
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        Claims claims = null;
        try{
            claims = Jwts
                    .parser()
                    .verifyWith(jwtProvider.getRSAKeys().publicKey())
                    .build()
                    .parseSignedClaims(token).getPayload();
        }
        catch (SignatureException e){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            APIError apiError = new APIError(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid JWT signature:" + e.getMessage(),
                    e.getStackTrace(), request);
            new ObjectMapper().writeValue(response.getWriter(), apiError);
        }catch (NullPointerException e){
            throw e;
        }

        String claimType = claims != null ?
                claims.get("claim_type",String.class) : null;
        if("refresh_token".equals(claimType)){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String username = jwtProvider.getUsername(token);
        if(!jwtProvider.validateToken(token, username)){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

}
