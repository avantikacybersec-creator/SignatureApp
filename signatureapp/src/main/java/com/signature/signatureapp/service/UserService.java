package com.signature.signatureapp.service;

import com.signature.signatureapp.dto.LoginRequest;
import com.signature.signatureapp.dto.RegisterRequest;
import com.signature.signatureapp.model.User;
import com.signature.signatureapp.repository.UserRepository;
import com.signature.signatureapp.security.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User register(RegisterRequest request) {

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        return userRepository.save(user);
    }

    public String login(LoginRequest request){

        User user =
                userRepository.findByEmail(
                        request.getEmail()
                ).orElseThrow(() -> new RuntimeException("Not found"));

        boolean matches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if(!matches){
            throw new RuntimeException(
                    "Invalid Password"
            );
        }

        return jwtService.generateToken(
                user.getEmail()
        );
    }
}