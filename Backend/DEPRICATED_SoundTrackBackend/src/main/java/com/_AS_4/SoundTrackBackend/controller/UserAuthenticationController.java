package com._AS_4.SoundTrackBackend.controller;

import com._AS_4.SoundTrackBackend.POJOs.Password;
import com._AS_4.SoundTrackBackend.POJOs.UserRequestData;
import com._AS_4.SoundTrackBackend.model.User;
import com._AS_4.SoundTrackBackend.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.util.Date;

@RestController
public class UserAuthenticationController {

    @Autowired
    private SecretKey jwtSecretKey;

    private static final long EXPIRATION_MILLIS = 1200000;

    @Autowired
    UserRepository userRepository;

    @PostMapping("userAuth/request")
    String authenticationRequest(@RequestBody UserRequestData requestingUser){
        if (!userRepository.existsByUsername(requestingUser.getUsername())){
            return "User does not exist; please check your spelling and try again: " + requestingUser.getUsername();
        }
        //If user exists, check the password credentials.
        User user = userRepository.findByUsername(requestingUser.getUsername());

        String hashedPassword = user.getHashedPassword();
        if(Password.verifyPassword(requestingUser.getPassword(), hashedPassword)){
            Date now = new Date();
            Date expiration = new Date(now.getTime() + EXPIRATION_MILLIS); //one week from issue
            String subject = user.getUsername();
            return Jwts.builder()
                    .setSubject(subject)
                    .setIssuedAt(now)
                    .setExpiration(expiration)
                    .signWith(jwtSecretKey, SignatureAlgorithm.HS256)
                    .compact();
        } else {
            return "Incorrect password: " + requestingUser.getPassword() + " for user: " + user.getUsername();
        }
    }

}
