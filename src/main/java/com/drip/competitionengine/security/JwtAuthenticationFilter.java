package com.drip.competitionengine.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwt;
    private final JwtEntryPoint entryPoint;     // <-- внедряем явно

    @Override
    protected void doFilterInternal(HttpServletRequest  req,
                                    HttpServletResponse res,
                                    FilterChain         chain)
            throws ServletException, IOException {

        String hdr = req.getHeader(HttpHeaders.AUTHORIZATION);

        if (hdr != null && hdr.startsWith("Bearer ")) {
            try {
                Claims claims = jwt.parse(hdr);                 // проверит exp + подпись

                @SuppressWarnings("unchecked")
                List<String> roles = claims.get("roles", List.class);

                @SuppressWarnings("unchecked")
                List<SimpleGrantedAuthority> authorities = roles == null
                        ? List.of()
                        : roles.stream()
                        .map(SimpleGrantedAuthority::new)   // "ROLE_USER" -> SimpleGrantedAuthority
                        .toList();

                Authentication auth = UsernamePasswordAuthenticationToken
                        .authenticated(claims.getSubject(), null, authorities);

                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (io.jsonwebtoken.ExpiredJwtException ex) {
                entryPoint.commence(
                        req, res,
                        new BadCredentialsException("JWT expired", ex)  // ← нужный текст
                );
                return;
            } catch (io.jsonwebtoken.JwtException ex) {
                entryPoint.commence(
                        req, res,
                        new BadCredentialsException("JWT invalid", ex)
                );
                return;
            }
        }
        chain.doFilter(req, res);   // дошли сюда — токен валиден или его нет
    }
}
