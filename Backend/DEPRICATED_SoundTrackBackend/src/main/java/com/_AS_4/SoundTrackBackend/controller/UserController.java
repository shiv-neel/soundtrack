package com._AS_4.SoundTrackBackend.controller;


import com._AS_4.SoundTrackBackend.POJOs.FriendRequestUsernames;
import com._AS_4.SoundTrackBackend.model.FriendRequest;
import com._AS_4.SoundTrackBackend.model.User;
import com._AS_4.SoundTrackBackend.repository.UserRepository;
import com._AS_4.SoundTrackBackend.POJOs.UserRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserController {
    @Autowired //this is the database we wanna work with:
    UserRepository userRepository;

    @PostMapping("user/createNew")
    User createNewUser(@RequestBody UserRequestData newUserData){
        if (userRepository.existsByUsername(newUserData.getUsername())){
            return null; //return a message for frontend? popup?
        } else if (userRepository.existsByEmail((newUserData.getEmail()))){
            return null; //return a message for frontend? popup?
        }
        User newUser = new User(newUserData.getAccountName(), newUserData.getUsername(), newUserData.getEmail(), newUserData.getPassword());
        userRepository.save(newUser);
        return newUser;
    }

    @PostMapping("user/findUser")
    String findUserByName(@RequestBody String targetUser){
        if (!userRepository.existsByUsername(targetUser)){
            return "User does not exist; please check your spelling and try again: " + targetUser;
        }
        User foundUser = userRepository.findByUsername(targetUser);
        return "success; user " + foundUser.getUsername() + " was created" ;
    }

    @PostMapping("user/sendFriendRequest")
    String sendFriendRequest(@RequestBody FriendRequestUsernames users){
        if (!userRepository.existsByUsername(users.getSender()) || !userRepository.existsByUsername(users.getReceiver())){
            String response = "The following users do not exist:";
            if (!userRepository.existsByUsername(users.getSender())) {
                response = response + " sender: " + users.getSender();
            }
            if (!userRepository.existsByUsername(users.getReceiver())) {
                response = response + " receiver: " + users.getSender();
            }
            return response;
        }

        User sender = userRepository.findByUsername(users.getSender());
        User receiver = userRepository.findByUsername(users.getReceiver());

        FriendRequest newRequest = new FriendRequest(sender);

        if (!(receiver.addFriendRequest(newRequest))) {
            return "There is already a pending request.";
        }
        userRepository.save(receiver);
        return "User " + users.getSender() + " successfully requested user " + users.getReceiver();
    }

}
