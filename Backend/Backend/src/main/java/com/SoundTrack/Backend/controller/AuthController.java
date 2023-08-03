package com.SoundTrack.Backend.controller;


import com.SoundTrack.Backend.dto.AuthResponseDto;
import com.SoundTrack.Backend.dto.LoginDto;
import com.SoundTrack.Backend.dto.RegisterDto;
import com.SoundTrack.Backend.model.Role;
import com.SoundTrack.Backend.model.UserEntity;
import com.SoundTrack.Backend.repository.RoleRepository;
import com.SoundTrack.Backend.repository.UserRepository;
import com.SoundTrack.Backend.security.JWTGenerator;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    //Bring in our JWT generator
    private JWTGenerator jwtGenerator;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository, RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder, JWTGenerator jwtGenerator) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
    }
    @Operation(summary = "Method to log the user in with provided credentials.",
            description = "This method accepts a request body of a 'LoginDto' data transfer object, which consists of the username and password in " +
                    "string format. If the credentials are correct, the method returns an AuthResponseDto data transfer object, which consists of the authenticated " +
                    "user's JSON Web Token and a string that logs the token type ('bearer' always as of right now). The JWT is to be stored on the client side and will " +
                    "be required for most requests in the future. (by Ian)")
    @PostMapping("login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto){
        //Use authentication manager from SecurityConfig
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(),
                loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication); //where security context will be. This will hold all authentication details.

        String token = jwtGenerator.generateToken(authentication);

        return new ResponseEntity<>(new AuthResponseDto(token), HttpStatus.OK);
    }
    @Operation(summary = "Method to register a new user.",
            description = "This method accepts a RegisterDto data transfer object that contains pertinent data to register a new user account in our app. " +
                    "The method will return 'Bad Request' http response if the username or email is already in use with another account. " +
                    "On successful registration, a new user account will be created with the provided user credentials; the password will be salted and " +
                    "hashed before being stored on the database. The user will then have to log in with the credentials to access secured requests. (by Ian)")
    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto){
        if (userRepository.existsByUsername(registerDto.getUsername())){
            return new ResponseEntity<>("Username is taken!", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.existsByEmail(registerDto.getEmail())){
            return new ResponseEntity<>("Email is used by another account!", HttpStatus.BAD_REQUEST);
        }
        //Create user IN MEMORY
        UserEntity user = new UserEntity();
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode((registerDto.getPassword())));

        Role roles = roleRepository.findByName("USER").get();
        user.setRoles(Collections.singletonList(roles));

        userRepository.save(user);

        return new ResponseEntity<>("User registered success!", HttpStatus.OK);
    }
    @Operation(summary = "Method to create a new admin",
            description = "This method accepts a 'RegisterDto' data transfer object to create a new administrator account with the provided credentials. " +
                    "On success, a new user with role 'ADMIN' will be created. (by Ian)")
    @PostMapping("admin")
    public ResponseEntity<String> createAdmin(@RequestBody RegisterDto registerDto){
        if (userRepository.existsByUsername(registerDto.getUsername())){
            return new ResponseEntity<>("Username is taken!", HttpStatus.BAD_REQUEST);
        }

        //Create user IN MEMORY
        UserEntity user = new UserEntity();
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode((registerDto.getPassword())));

        Role roles = roleRepository.findByName("ADMIN").get();
        user.setRoles(Collections.singletonList(roles));

        userRepository.save(user);

        return new ResponseEntity<>("User registered success!", HttpStatus.OK);
    }
}
