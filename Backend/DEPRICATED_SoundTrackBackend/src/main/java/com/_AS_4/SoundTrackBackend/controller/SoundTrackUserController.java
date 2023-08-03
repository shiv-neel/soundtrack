package com._AS_4.SoundTrackBackend.controller;

import com._AS_4.SoundTrackBackend.model.SoundTrackUser;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * Will become controller for admin user account
 */

@RestController
public class SoundTrackUserController {
    HashMap<String, SoundTrackUser> userList = new HashMap<>();

    @GetMapping("/Users")
    public @ResponseBody HashMap<String, SoundTrackUser> getUserList(){
        return userList;
    }

    @PostMapping("/Users")
    public @ResponseBody String createUser(@RequestBody SoundTrackUser user){
        System.out.println(user);
        userList.put(user.getName(), user);
        return "New SoundTrack user " + user.getName() + " added and saved.";
    }

    @GetMapping("/Users/{name}")
    public @ResponseBody SoundTrackUser getUser(@PathVariable String name){
        SoundTrackUser s = userList.get(name);
        return s;
    }

    @PutMapping("/Users/{name}")
    public @ResponseBody SoundTrackUser updateUser(@PathVariable String name, @RequestBody SoundTrackUser s){
        userList.replace(name, s);
        return userList.get(name);
    }

    @DeleteMapping("/Users/{name}")
    public @ResponseBody HashMap<String, SoundTrackUser> deleteUser(@PathVariable String name){
        userList.remove(name);
        return userList;
    }
}
