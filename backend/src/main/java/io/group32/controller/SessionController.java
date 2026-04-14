package io.group32.controller;

import io.group32.model.User;
import io.group32.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/api/session/me")
    public ResponseEntity<User> getMe(HttpServletRequest request) {
        User user = sessionService.getUser(request);
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(user);
    }
}
