package com.SoundTrack.Backend.controller;


import com.SoundTrack.Backend.dto.SearchDto;
import com.SoundTrack.Backend.model.CuratorRequest;
import com.SoundTrack.Backend.model.FriendRequest;
import com.SoundTrack.Backend.model.Role;
import com.SoundTrack.Backend.model.UserEntity;
import com.SoundTrack.Backend.repository.RoleRepository;
import com.SoundTrack.Backend.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/adminFunction")
public class AdminFunctionController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Operation(summary = "function for deleting users from the user repository and database",
            description = "When a user breaks any rules while using the app such as harassment, bullying, etc., any admin is able to use this method" +
                    " to remove the user and their data. (by Susanna)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "success"),
                    @ApiResponse(responseCode = "500", description = "internal server error"),
                    @ApiResponse(responseCode = "400", description = "bad request")
    })
    @DeleteMapping ("deleteUser/{username}")
    @Secured({"ADMIN"}) // List all user roles that are allowed to access this request at the method level.
    public ResponseEntity<String> deleteUser(@PathVariable String username){
        if (!userRepository.existsByUsername(username)){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        UserEntity badUser;

        try {
            badUser = userRepository.findByUsername(username).get();
        } catch (NoSuchElementException e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //nullify all relationships possible

        badUser.removeAllFriendRequests(); //nullify friendRequests

        badUser.removeAllUserPosts(); //nullify userPosts

        badUser.removeAllFriends(); //nullify friends

        badUser.removeAllFollowers(); //nullify followers

        badUser.removeAllFollowings(); //nullify followings

        //save the updates
        userRepository.save(badUser);

        //delete the user
        userRepository.delete(badUser);

        return new ResponseEntity<>("Success: user " + badUser.getUsername() + " deleted from repository", HttpStatus.OK);
    }

}

