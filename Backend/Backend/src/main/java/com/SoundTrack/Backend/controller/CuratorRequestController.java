package com.SoundTrack.Backend.controller;

import com.SoundTrack.Backend.dto.CuratorRequestDto;
import com.SoundTrack.Backend.dto.FriendRequestDto;
import com.SoundTrack.Backend.model.CuratorRequest;
import com.SoundTrack.Backend.model.FriendRequest;
import com.SoundTrack.Backend.model.Role;
import com.SoundTrack.Backend.model.UserEntity;
import com.SoundTrack.Backend.repository.CuratorRequestRepository;
import com.SoundTrack.Backend.repository.RoleRepository;
import com.SoundTrack.Backend.repository.UserRepository;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/curatorRequests")
public class CuratorRequestController {

    @Autowired
    private CuratorRequestRepository curatorRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    // Endpoint to get the public list
    @GetMapping("getPendingCuratorRequests")
    @Secured("ADMIN")
    public List<CuratorRequestDto> getCuratorRequests() {
        List<CuratorRequest> curatorRequests = curatorRequestRepository.findAll();

        List<CuratorRequestDto> requestDtos = new ArrayList<>();
        //temp variable
        CuratorRequest request;

        for (int i = 0; i < curatorRequests.size(); i++){
            request = curatorRequests.get(i);
            CuratorRequestDto requestDto = new CuratorRequestDto(request.getId(), request.getNewCurator().getFullName(), request.getNewCurator().getUsername());
            requestDtos.add(requestDto);
        }

        return requestDtos;
    }

    // Endpoint to add an item to the public list
    @PostMapping("requestCurator")
    @Secured("USER")
    public ResponseEntity<String> requestCurator() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        UserEntity currentUser;

        try {
            currentUser = userRepository.findByUsername(userDetails.getUsername()).get();
        } catch (NoSuchElementException e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        CuratorRequest curatorRequest = new CuratorRequest();
        curatorRequest.setNewCurator(currentUser);

        curatorRequestRepository.save(curatorRequest);

        return new ResponseEntity<>("success! Request to become a curator has been sent", HttpStatus.OK);
    }

    @GetMapping("findCuratorRequest/{senderUsername}")
    @Secured("ADMIN")
    public CuratorRequest findCuratorRequest(String senderUsername){
        List<CuratorRequest> curatorRequestList = curatorRequestRepository.findAll();
        for (CuratorRequest f: curatorRequestList){
            //if a curatorRequest has a matching user then it is the correct one
            if (f.getNewCurator().getUsername().equals(senderUsername)){
                return f;
            }
        }
        return null;
    }

    @PostMapping("acceptCuratorRequest/{senderUsername}")
    @Secured("ADMIN")
    public ResponseEntity<String> acceptCuratorRequest(@PathVariable String senderUsername){
        if (!userRepository.existsByUsername(senderUsername)){
            return new ResponseEntity<>("Sender does not exist.", HttpStatus.BAD_REQUEST);
        }
        //Else: target user exists.
        //Get the sender user's Authentication and User Details from the SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        //Create users in memory
        UserEntity sender;

        try {
            sender = userRepository.findByUsername(senderUsername).get();
        } catch (NoSuchElementException e){
            // 404 error
            return new ResponseEntity<>("Database error: sender could not be loaded into memory.", HttpStatus.NOT_FOUND);
        }

        Role curatorRole = roleRepository.findByName("CURATOR").get();

        List<Role> userRoles = new ArrayList<>();
        userRoles.add(curatorRole);

        //change user's userType to be Curator versus user
        sender.setRoles(userRoles);

        CuratorRequest request = findCuratorRequest(senderUsername);

        removeCuratorRequest(request);

        userRepository.save(sender);

        return new ResponseEntity<>("Success, user " + sender.getUsername() + " is now a curator", HttpStatus.OK);
    }

    @PostMapping("declineCuratorRequest/{senderUsername}")
    @Secured("ADMIN")
    public ResponseEntity<String> declineCuratorRequest(@PathVariable String senderUsername){
        if (!userRepository.existsByUsername(senderUsername)){
            return new ResponseEntity<>("Sender does not exist.", HttpStatus.BAD_REQUEST);
        }
        //Else: target user exists.
        //Get the sender user's Authentication and User Details from the SecurityContextHolder
        //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        //Create users in memory
        UserEntity sender;

        try {
            sender = userRepository.findByUsername(senderUsername).get();
        } catch (NoSuchElementException e){
            // 404 error
            return new ResponseEntity<>("Database error: sender could not be loaded into memory.", HttpStatus.NOT_FOUND);
        }

        CuratorRequest request = findCuratorRequest(senderUsername);

        removeCuratorRequest(request);

        return new ResponseEntity<>("Success, request from user " + sender.getUsername() + " has been declined and removed", HttpStatus.OK);
    }

    public void removeCuratorRequest(CuratorRequest curatorRequest){
        curatorRequest.setNewCurator(null);
        curatorRequestRepository.delete(curatorRequest);
    }

}
