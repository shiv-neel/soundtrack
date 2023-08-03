package com.SoundTrack.Backend.controller;


import com.SoundTrack.Backend.dto.AuthResponseDto;
import com.SoundTrack.Backend.dto.RelationshipDto;
import com.SoundTrack.Backend.model.*;
import com.SoundTrack.Backend.repository.FriendRequestRepository;
import com.SoundTrack.Backend.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/userRelationships/")
public class UserRelationshipController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendRequestRepository friendRequestRepository;
    @Operation(summary = "allows user to send a friend request to another user",
            description = "When a user wishes to be friends with another user, they can send a friend request to that user. They must wait until that" +
                    " user accepts the friend request (different method) so that the friendship can be created. The current user is set as the sender" +
                    " within the friend request and the user they are sending the request to is set to be the receiver. (by Susanna)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "success"),
                    @ApiResponse(responseCode = "500", description = "internal server error"),
                    @ApiResponse(responseCode = "400", description = "bad request")
    })
    @PostMapping("sendFriendRequest/{receiverUsername}")
    @Secured("USER") // List all user roles that are allowed to access this request at the method level.
    public ResponseEntity<String> sendFriendRequest(@PathVariable String receiverUsername){
        if (!userRepository.existsByUsername(receiverUsername)){
            return new ResponseEntity<>("Receiver does not exist.", HttpStatus.BAD_REQUEST);
        }
        //Else: target user exists.
        //Get the sender user's Authentication and User Details from the SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        //Create users in memory
        UserEntity sender;
        UserEntity receiver;
        try {
            sender = userRepository.findByUsername(userDetails.getUsername()).get();
            receiver = userRepository.findByUsername(receiverUsername).get();
        } catch (NoSuchElementException e){
            return new ResponseEntity<>("Database error: either current user or receiver could not be loaded into memory.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //Check if there's already a friend request pending
        if ((receiver.hasPendingFriendRequest(sender.getUsername()))){
            return new ResponseEntity<>(receiver.getUsername() + " is already friends with the current user.", HttpStatus.OK);
        }

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setReceiver(receiver);
        //Add the friend request
        receiver.addFriendRequest(friendRequest);
        userRepository.save(receiver);

        return new ResponseEntity<>("Success: user " + receiver.getUsername() + " was sent a friend request.", HttpStatus.OK);
    }

    @Operation(summary = "allows user to accept an incoming friend request from another user",
            description = "When a user has a pending friend request, they have the choice to accept or decline it. This method accepts it and adds the" +
                    " sender of the friend request as a friend (and vice versa). This creates two friendship relationships between the two users. One for" +
                    " each user as a sender and the other as the receiver. (by Susanna)",
            responses = {
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "404", description = "user(s) not found"),
            @ApiResponse(responseCode = "400", description = "bad request")
    })
    @PostMapping("acceptFriendRequest/{senderUsername}") //sender id because usernames will be allowed to change
    @Secured("USER") // List all user roles that are allowed to access this request at the method level.
    public ResponseEntity<String> acceptFriendRequest(@PathVariable String senderUsername){
        if (!userRepository.existsByUsername(senderUsername)){
            return new ResponseEntity<>("Sender does not exist.", HttpStatus.BAD_REQUEST);
        }
        //Else: target user exists.
        //Get the sender user's Authentication and User Details from the SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        //Create users in memory
        UserEntity sender;
        UserEntity receiver;
        try {
            sender = userRepository.findByUsername(senderUsername).get();
            receiver = userRepository.findByUsername(userDetails.getUsername()).get();
        } catch (NoSuchElementException e){
            // 404 error
            return new ResponseEntity<>("Database error: either current user or receiver could not be loaded into memory.", HttpStatus.NOT_FOUND);
        }

        //create friendship from current user's perspective
        Friendship currentUserFriendship = new Friendship();
        currentUserFriendship.setCurrentUser(receiver);
        currentUserFriendship.setFriendUser(sender);

        //create friendship from the other user's perspective
        Friendship otherUserFriendship = new Friendship();
        otherUserFriendship.setCurrentUser(sender);
        otherUserFriendship.setFriendUser(receiver);
        //Add receiver as a friend to the sender and add the sender as a friend to the receiver
        receiver.addFriend(currentUserFriendship);
        sender.addFriend(otherUserFriendship);
        //remove the friend request so it is no longer in the list
        FriendRequest request = receiver.findFriendRequest(sender, receiver);
        receiver.removeFriendRequest(request);

        userRepository.save(receiver);
        userRepository.save(sender);

        return new ResponseEntity<>("Success: user " + receiver.getUsername() + " and " + sender.getUsername() + " are now friends.", HttpStatus.OK);
    }


    @Operation(summary = "allows user to decline an incoming friend request from another user",
            description = "When a user has a pending friend request, they have the choice to accept or decline it. This method declines it and deletes" +
                    " the friend request from the receiver's pending friendRequest list. The friendRequest does not live on the other user (the sender)" +
                    " so there is no need to remove it from that user. (by Susanna)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "success"),
                    @ApiResponse(responseCode = "404", description = "user(s) not found"),
                    @ApiResponse(responseCode = "400", description = "bad request")
    })
    @PostMapping("declineFriendRequest/{senderUsername}") //sender id because usernames can change
    @Secured("USER") // List all user roles that are allowed to access this request at the method level.
    public ResponseEntity<String> declineFriendRequest(@PathVariable String senderUsername){
        if (!userRepository.existsByUsername(senderUsername)){
            return new ResponseEntity<>("Sender does not exist.", HttpStatus.BAD_REQUEST);
        }
        //Else: target user exists.
        //Get the sender user's Authentication and User Details from the SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        //Create users in memory
        UserEntity sender;
        UserEntity receiver;
        try {
            sender = userRepository.findByUsername(senderUsername).get();
            receiver = userRepository.findByUsername(userDetails.getUsername()).get();
        } catch (NoSuchElementException e){
            // 404 error
            return new ResponseEntity<>("Database error: either current user or receiver could not be loaded into memory.", HttpStatus.NOT_FOUND);
        }

        //nullifies the friend request between the sender and receiver.
        FriendRequest request = receiver.findFriendRequest(sender, receiver);

        receiver.removeFriendRequest(request);
        userRepository.save(receiver);
        userRepository.save(sender);

        return new ResponseEntity<>("Success: friend request from "+ sender.getUsername() + " was removed.", HttpStatus.OK);
    }

    @PostMapping("followCurator/{curatorUsername}")
    @Secured("USER") // List all user roles that are allowed to access this request at the method level.
    public ResponseEntity<String> followCurator(@PathVariable String curatorUsername){
        if (!userRepository.existsByUsername(curatorUsername)){
            return new ResponseEntity<>("Curator does not exist.", HttpStatus.BAD_REQUEST);
        }
        //Else: target user exists.

        //Get the current user's Authentication and User Details from the SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        //Create users in memory
        UserEntity curator;
        UserEntity currentUser;

        try {
            curator = userRepository.findByUsername(curatorUsername).get();
            currentUser = userRepository.findByUsername(userDetails.getUsername()).get();
        } catch (NoSuchElementException e){
            // 404 error
            return new ResponseEntity<>("Database error: either current user or curator could not be loaded into memory.", HttpStatus.NOT_FOUND);
        }

        //create relationships
        FollowerRelationship followerRelationship = new FollowerRelationship();
        FollowingRelationship followingRelationship = new FollowingRelationship();

        //set follower details
        followerRelationship.setFollowerUser(currentUser);
        followerRelationship.setCurrentCurator(curator);

        //set following details
        followingRelationship.setCuratorUser(curator);
        followingRelationship.setCurrentUser(currentUser);

        //add follower for curator and add following for the user
        curator.addFollower(followerRelationship);
        currentUser.addFollowing(followingRelationship);

        userRepository.save(curator);
        userRepository.save(currentUser);

        return new ResponseEntity<>("Success: user " + currentUser.getUsername() + " is now following " + curator.getUsername() + ".", HttpStatus.OK);
    }
}
