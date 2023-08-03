package com._AS_4.SoundTrackBackend.Beans;
/*
 This dude eatin BEEEEAAAANSS
 */

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secretKey;

    @Bean
    public SecretKey jwtSecretKey() {
        byte[] decodedKey = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(decodedKey);
    }
}
