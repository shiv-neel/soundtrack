package com.SoundTrack.Backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTAuthenticationFilter extends OncePerRequestFilter {

    /*
            Before entering the security chain, performs a check to see if there is a token within the header.
     */
    @Autowired
    private JWTGenerator tokenGenerator;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = getJWTFromRequest(request);
        if (StringUtils.hasText(token) && tokenGenerator.validateToken(token)){
            String username = tokenGenerator.getUsernameFromJWT(token);
            //get user details from our custom User Details Service
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            //now authenticate it
            //null means we dont need credentials
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            token,
                            userDetails.getAuthorities());
            //set the authentication details
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        //Filter chain
        //Chain of responsibility pattern, therefore pass request and response so it can reach the next level of the chain
        filterChain.doFilter(request, response);
    }

    private String getJWTFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        //use string utils to find the token
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;

    }
}
