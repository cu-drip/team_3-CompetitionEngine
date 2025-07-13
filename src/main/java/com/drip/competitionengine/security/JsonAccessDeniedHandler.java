package com.drip.competitionengine.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JsonAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest  req,
                       HttpServletResponse res,
                       AccessDeniedException ex) throws IOException {

        res.setStatus(HttpStatus.FORBIDDEN.value());          // 403
        res.setContentType("application/json");
        res.getWriter().print("""
            {"error":"Forbidden - need admin rights"}
            """);
        res.flushBuffer();    // ← фиксируем ответ, чтобы его больше никто не трогал
    }
}
