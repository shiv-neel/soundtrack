package com.SoundTrack.Backend.controller;

import com.SoundTrack.Backend.dto.CuratorRequestDto;
import com.SoundTrack.Backend.dto.FriendRequestDto;
import com.SoundTrack.Backend.model.CuratorRequest;
import com.SoundTrack.Backend.model.FriendRequest;
import com.SoundTrack.Backend.model.UserEntity;
import com.SoundTrack.Backend.repository.FriendRequestRepository;
import com.SoundTrack.Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/Notifications/")
public class NotificationsController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @GetMapping("getPendingFriendRequests/{currentUsername}")
    @Secured({"USER", "CURATOR"})
    public ResponseEntity<List<FriendRequestDto>> getPendingFriendRequests(@PathVariable String currentUsername){
        if (!userRepository.existsByUsername(currentUsername)){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        //Get the sender user's Authentication and User Details from the SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        //Create users in memory
        UserEntity receiver;

        try {
            receiver = userRepository.findByUsername(userDetails.getUsername()).get();
        } catch (NoSuchElementException e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        List<FriendRequest> pendingRequests;
        pendingRequests = receiver.getFriendRequests(receiver);

        List<FriendRequestDto> requestDtos = new ArrayList<>();
        //temp variable
        FriendRequest request;

        for (int i = 0; i < pendingRequests.size(); i++){
            request = pendingRequests.get(i);
            FriendRequestDto requestDto = new FriendRequestDto(request.getId(), request.getSender().getId(), request.getSender().getUsername());
            requestDtos.add(requestDto);
        }

        //replace the below when it works
        return new ResponseEntity<>(requestDtos, HttpStatus.OK);
    }

}
