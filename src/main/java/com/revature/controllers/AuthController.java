package com.revature.controllers;

import com.revature.dtos.LoginRequest;
import com.revature.dtos.RegisterRequest;
import com.revature.models.User;
import com.revature.services.AuthService;
import com.revature.utils.JWTUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {


	  @Autowired
    private AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        Optional<User> optional = authService.findByCredentials(loginRequest.getEmail(), loginRequest.getPassword());

        if (!optional.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        User u = optional.get();
        String res = String.format("{\"id\": \"%d\", \"username\": \"%s\", \"email\": \"%s\", \"token\": \"%s\"}",
                u.getId(), u.getUsername(), u.getEmail(), JWTUtil.generateUserToken(u));

        return ResponseEntity.ok(res);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        User created = new User(
                registerRequest.getFirstname(),
                registerRequest.getLastname(),
                registerRequest.getUsername(),
                registerRequest.getPassword(),
                registerRequest.getEmail());
        try {
            authService.register(created);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Unique Constraint violated, emails must be unique.");
        }

    }
}
