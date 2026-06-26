package com.badminton.controller;

import com.badminton.dto.UserDTO;
import com.badminton.dto.request.RegisterRequest;
import com.badminton.dto.response.ResponseDTO;
import com.badminton.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<UserDTO>> register(@Valid @RequestBody RegisterRequest request) {
        UserDTO registeredUser = authService.register(request);
        ResponseDTO<UserDTO> response = ResponseDTO.<UserDTO>builder()
                .success(true)
                .message("User registered successfully")
                .data(registeredUser)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
