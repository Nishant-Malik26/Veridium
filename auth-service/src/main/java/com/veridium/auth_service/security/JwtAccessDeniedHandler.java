package com.veridium.auth_service.security;

import com.veridium.auth_service.constants.Constants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.veridium.auth_service.constants.Constants.FORBIDDEN;
import static com.veridium.auth_service.constants.ErrorMessages.USER_DONT_HAVE_PERMISSION;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        final Map<String, Object> body = new HashMap<>();
        body.put(Constants.STATUS, HttpServletResponse.SC_FORBIDDEN);
        body.put(Constants.ERROR, FORBIDDEN);
        body.put(Constants.MESSAGE, USER_DONT_HAVE_PERMISSION);
        body.put(Constants.PATH, request.getServletPath());

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}
