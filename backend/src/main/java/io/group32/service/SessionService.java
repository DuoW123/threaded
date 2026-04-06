package io.group32.service;

import io.group32.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionService {
    public void createSession(User user, HttpServletRequest request) {
        //The password is passed as null because by the time this method is used the password has already been checked
        UsernamePasswordAuthenticationToken sessionToken = new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(sessionToken);

        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", context);
    }

    public User getUser(HttpServletRequest request) {
        SecurityContext context = SecurityContextHolder.getContext();

        if (context.getAuthentication() != null && context.getAuthentication().isAuthenticated()) {
            Object principal = context.getAuthentication().getPrincipal();
            if (principal instanceof User) {
                return (User) principal;
            }
        }

        return null;
    }
}