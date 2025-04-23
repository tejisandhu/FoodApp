package com.ribjet.FoodApp.Config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            String requestUri = request.getRequestURI();
            String queryString = request.getQueryString();
            // Append query params if present
            if (queryString != null) {
                requestUri += "?" + queryString;
            }
            // Ensure session exists before storing redirect URL
            session = request.getSession(true);
            session.setAttribute("redirectAfterLogin", requestUri);
            System.out.println("Stored redirect URL: " + requestUri); // Debugging
            response.sendRedirect("/auth/login");
            return false;
        }
        return true;
    }
}
