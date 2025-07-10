package com.drip.competitionengine.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest  req,
                         HttpServletResponse res,
                         AuthenticationException ex) throws IOException {

        res.setHeader(HttpHeaders.WWW_AUTHENTICATE,
                "Bearer error=\"invalid_token\", error_description=\"" + ex.getMessage() + '"');
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);       // 401
        res.setContentType("application/json");
        res.getWriter().printf("{\"error\":\"%s\"}", ex.getMessage());
    }
}
