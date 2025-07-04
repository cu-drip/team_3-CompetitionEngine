package com.drip.competitionengine.security;

import com.drip.competitionengine.repo.UserRepository;
import com.drip.competitionengine.model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * Simple header‑based auth: validates presence & format of X‑User‑UUID / X‑User‑Hash.
 * (No real password check – assumes upstream gateway already did it.)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class HeaderAuthFilter implements Filter {
    private static final String H_USER = "X-User-UUID";
    private static final String H_HASH = "X-User-Hash";

    private final UserRepository userRepo;

    @Override
    public void doFilter(ServletRequest r, ServletResponse s, FilterChain c)
            throws IOException, ServletException {

        HttpServletRequest  req  = (HttpServletRequest) r;
        HttpServletResponse res  = (HttpServletResponse) s;
        String path = req.getRequestURI();

        if (path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-ui")   ||
                path.equals("/swagger-ui.html")) {
            c.doFilter(r, s);      // пускаем без проверок
            return;
        }
        // 1. валидация заголовков -----------------------------
        String userHdr = req.getHeader(H_USER);
        String hashHdr = req.getHeader(H_HASH);
        if (userHdr == null || hashHdr == null) {
            sendError(res, 401, "missing auth headers");
            return;
        }
        UUID userId;
        try { userId = UUID.fromString(userHdr); }
        catch (IllegalArgumentException e) {
            sendError(res, 400, "insufficient permissions");
            return;
        }
        if (hashHdr.length() < 32 || hashHdr.length() > 128) {
            sendError(res, 400, "insufficient permissions");
            return;
        }

        // 2. проверка в БД ------------------------------------
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) {
            sendError(res, 401, "insufficient permissions");
            return;
        }
        if (!hashHdr.equals(user.getHashedPassword())) {          // или BCrypt.checkpw(...)
            sendError(res, 401, "insufficient permissions");
            return;
        }

        if (!user.isAdmin()) sendError(res, 401, "insufficient permissions");

        // 3. всё ок → дальше по цепочке -----------------------
        req.setAttribute("userId", userId);
        c.doFilter(r, s);

    }

    private static void sendError(HttpServletResponse res, int code, String msg) throws IOException {
        res.setStatus(code);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.getWriter()
                .printf("{\"status\":%d,\"error\":\"%s\"}", code, msg)
                .flush();
        log.warn("Auth rejected: {}", msg);
    }
}
