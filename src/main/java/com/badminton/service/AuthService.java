package com.badminton.service;

import com.badminton.dto.UserDTO;
import com.badminton.dto.request.RegisterRequest;

public interface AuthService {
    UserDTO register(RegisterRequest request);
}
