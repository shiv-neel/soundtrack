package com.SoundTrack.Backend.controller;


import com.SoundTrack.Backend.dto.SearchDto;
import com.SoundTrack.Backend.model.FriendRequest;
import com.SoundTrack.Backend.model.UserEntity;
import com.SoundTrack.Backend.repository.FriendRequestRepository;
import com.SoundTrack.Backend.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/findUser")
public class FindUserController {

    @Autowired
    private UserRepository userRepository;

    @Operation(summary = "method to use when searching for another user",
            description = "If a user wants to look at the profile of another user or send a friend request, they would first have to look up the user." +
                    " The user's information is sent back as a Data Transfer Object with information such as their name, username, user type, and " +
                    " profile picture. (by Susanna)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "success"),
                    @ApiResponse(responseCode = "500", description = "internal server error"),
                    @ApiResponse(responseCode = "400", description = "bad request")
            })
    @PostMapping("searchUsername/{username}")
    @Secured({"USER", "ADMIN"}) // List all user roles that are allowed to access this request at the method level.
    public ResponseEntity<SearchDto> searchForUser(@PathVariable String username){
        if (!userRepository.existsByUsername(username)){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        /*
                Commented the below Authentication and UserDetail objects out; while they are correct statements, we won't
                be needing any details of the sender for this method (at least not yet). Let me know if you have any questions!
         */

        //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        //Create user in memory
        UserEntity foundUser;

        try {
            foundUser = userRepository.findByUsername(username).get();
        } catch (NoSuchElementException e){
             return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //pull information from the found user to the "returnUser" with matching username,
        //full name, and profile picture
        SearchDto returnUser = new SearchDto();
        returnUser.setName(foundUser.getFullName());
        returnUser.setUsername(foundUser.getUsername());
        returnUser.setUserType(foundUser.getRoles().get(0).getName());
        returnUser.setProfilePicture(foundUser.getProfilePicture());

        return new ResponseEntity<>(returnUser, HttpStatus.OK);
    }
}
