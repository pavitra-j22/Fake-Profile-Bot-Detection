package com.socialshield.controller;

import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class AppErrorController implements ErrorController {

    private final ObjectMapper mapper = new ObjectMapper();

    @RequestMapping("/error")
    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, jakarta.servlet.ServletException {
        Integer statusAttr = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int code = statusAttr == null ? HttpServletResponse.SC_INTERNAL_SERVER_ERROR : statusAttr;
        String uri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        String accept = request.getHeader("Accept");

        boolean browser = accept != null && accept.contains("text/html");
        boolean api = uri != null && (uri.startsWith("/api") || uri.equals("/health"));

        if (browser && !api && !hasFileExtension(uri)) {
            response.setStatus(HttpServletResponse.SC_OK);
            request.getRequestDispatcher("/index.html").forward(request, response);
            return;
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", code);
        body.put("error", HttpStatus.resolve(code) != null ? HttpStatus.resolve(code).getReasonPhrase() : "Error");
        body.put("path", uri == null ? "" : uri);

        response.setStatus(code);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        mapper.writeValue(response.getWriter(), body);
    }

    private boolean hasFileExtension(String uri) {
        if (uri == null || uri.equals("/")) return false;
        String segment = uri.substring(uri.lastIndexOf('/') + 1);
        return segment.contains(".");
    }
}
