package com.SoundTrack.Backend.controller;

import com.SoundTrack.Backend.dto.*;

import com.SoundTrack.Backend.model.*;

import com.SoundTrack.Backend.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNullApi;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Operation(summary = "Get the JSON representation of the current user", description = "Returns a JSON that has all details of the current user (userId, username, email, " +
            "fullName, SpotifyUsername, role, and postIds). (by Ian)")
    @GetMapping("getCurrentUser")
    @Secured({"USER", "CURATOR", "ADMIN"})
    public ResponseEntity<UserObjectDto> getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        UserEntity currentUser;

        try {
            currentUser = userRepository.findByUsername(userDetails.getUsername()).get();
        } catch (NoSuchElementException e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        UserObjectDto returnJson = new UserObjectDto();

        returnJson.setId(currentUser.getId());
        returnJson.setUsername(currentUser.getUsername());
        returnJson.setEmail(currentUser.getEmail());
        returnJson.setFullName(currentUser.getFullName());
        returnJson.setSpotifyUsername(currentUser.getSpotifyUsername());
        returnJson.setRoles(currentUser.getRoles());
        returnJson.setUserPostIds(currentUser.getPostsAsIds());

        List<FriendRequestDto> pendingFriendRequests = new ArrayList<>();
        for (FriendRequest f: currentUser.getFriendRequests()){
            pendingFriendRequests.add(new FriendRequestDto(f.getId(), f.getSender().getId(), f.getSender().getUsername()));
        }
        returnJson.setPendingFriendRequests(pendingFriendRequests);

        //temporary patch (until we add methods to add/change pfp)
        returnJson.setProfilePicture("http://coms-309-026.class.las.iastate.edu/pfp/default.jpg");

        return new ResponseEntity<>(returnJson, HttpStatus.OK);
    }
    @Secured("USER")
    public ResponseEntity<Long> getCurrentUserMostRecentPost(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        UserEntity currentUser;

        try {
            currentUser = userRepository.findByUsername(userDetails.getUsername()).get();
        } catch (NoSuchElementException e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(currentUser.getPostsAsIds().get(currentUser.getPostsAsIds().size() - 1), HttpStatus.OK);
    }
    @Operation(summary = "Gets a list of all posts made by a given user.",
            description = "The list of posts is returned in the format of a list of Post IDs, which serve as the primary key for " +
                    "rendering posts. The 'userId' is the user ID (primary key) of the user whose posts you wish to retrieve" +
                    " and 'limit' is the maximum number of posts you wish to retrieve; these are passed in as path variables. (by Ian)")
    @GetMapping("getPostsByUserId/{userId}/{limit}")
    @Secured({"USER","CURATOR","ADMIN"})
    public ResponseEntity<LongListDto> getPostsByUserId(@PathVariable long userId, @PathVariable int limit){

        UserEntity targetUser;

        try {
            targetUser = userRepository.findById(userId).get();
        } catch (NoSuchElementException e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        LongListDto postListObject = new LongListDto();
        postListObject.setIds(targetUser.getPostsAsIds().subList(0, Math.min(limit, targetUser.getUserPosts().size())));

        return new ResponseEntity<>(postListObject, HttpStatus.OK);
    }


    @GetMapping("getUserFriends/{username}")
    @Secured({"USER", "CURATOR", "ADMIN"})
    public ResponseEntity<List<FriendshipDto>> getUserFriends(@PathVariable String username){
        if (!userRepository.existsByUsername(username)){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        UserEntity user;

        try {
            user = userRepository.findByUsername(username).get();
        } catch (NoSuchElementException e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        List<Friendship> userFriends = user.getFriends();

        List<FriendshipDto> friendshipDtos = new ArrayList<>();

        Friendship friendship;

        for (int i = 0; i < userFriends.size(); i++){
            friendship = userFriends.get(i);
            FriendshipDto friendshipDto = new FriendshipDto(friendship.getId(), friendship.getCurrentUser().getUsername(), friendship.getFriendUser().getUsername());
            friendshipDtos.add(friendshipDto);
        }

        return new ResponseEntity<>(friendshipDtos, HttpStatus.OK);
    }

    @GetMapping("getUserFollowings/{username}")
    @Secured({"USER", "CURATOR", "ADMIN"})
    public ResponseEntity<List<FollowingRelationshipDto>> getUserFollowings(@PathVariable String username){
        if (!userRepository.existsByUsername(username)){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        UserEntity user;

        try {
            user = userRepository.findByUsername(username).get();
        } catch (NoSuchElementException e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        List<FollowingRelationship> userFollowings = user.getFollowing();

        List<FollowingRelationshipDto> followingDtos = new ArrayList<>();

        FollowingRelationship followingRelationship;

        for (int i = 0; i < userFollowings.size(); i++){
            followingRelationship = userFollowings.get(i);
            FollowingRelationshipDto followingDto = new FollowingRelationshipDto(followingRelationship.getId(), followingRelationship.getCurrentUser().getUsername(), followingRelationship.getCuratorUser().getUsername());
            followingDtos.add(followingDto);
        }

        return new ResponseEntity<>(followingDtos, HttpStatus.OK);
    }

    @GetMapping("getUserFollowers/{username}")
    @Secured({"USER", "CURATOR", "ADMIN"})
    public ResponseEntity<List<FollowerRelationshipDto>> getUserFollowers(@PathVariable String username){
        if (!userRepository.existsByUsername(username)){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        UserEntity user;

        try {
            user = userRepository.findByUsername(username).get();
        } catch (NoSuchElementException e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        List<FollowerRelationship> userFollowers = user.getFollowers();

        List<FollowerRelationshipDto> followerDtos = new ArrayList<>();

        FollowerRelationship followerRelationship;

        for (int i = 0; i < userFollowers.size(); i++){
            followerRelationship = userFollowers.get(i);
            FollowerRelationshipDto followerDto = new FollowerRelationshipDto(followerRelationship.getId(), followerRelationship.getCurrentCurator().getUsername(), followerRelationship.getFollowerUser().getUsername());
            followerDtos.add(followerDto);
        }

        return new ResponseEntity<>(followerDtos, HttpStatus.OK);
    }


    @GetMapping("getFeed/{limit}")
    @Secured("USER")
    public ResponseEntity<ArrayList<ReturnPostDto>> getFeed(@PathVariable int limit){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserEntity user;
        try{
            user = userRepository.findByUsername(userDetails.getUsername()).get();
        } catch (NoSuchElementException e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        boolean noPostFound;
        List<Friendship> friends = user.getFriends();
        ArrayList<PostEntity> postFeed = new ArrayList<>();
        List<PostEntity> newestPostFeed = null;
        PostEntity newest = null;

        for (int i = 0; i < limit; i++){
            noPostFound = true;
            for (Friendship f : friends){
                if (f.getFriendUser().getUserPosts().size() > 0 &&
                        (newest == null || f.getFriendUser().getUserPosts().get(f.getFriendUser().getUserPosts().size() - 1).getCreatedAt().isAfter(newest.getCreatedAt()))){
                    newest = f.getFriendUser().getUserPosts().get(f.getFriendUser().getUserPosts().size() - 1);
                    newestPostFeed = f.getFriendUser().getUserPosts();
                    noPostFound = false;
                }

            }


            if (user.getUserPosts().size() > 0 &&
                    (newest==null || user.getUserPosts().get(user.getUserPosts().size() - 1).getCreatedAt().isAfter(newest.getCreatedAt()))){
                newest = user.getUserPosts().get(user.getUserPosts().size() - 1);
                newestPostFeed = user.getUserPosts();
                noPostFound = false;
            }


            if (noPostFound){
                break;
            }

            postFeed.add( newest);
            newestPostFeed.remove(newestPostFeed.size() - 1);

            newestPostFeed = null;
            newest = null;
            noPostFound = true;
        }

        ArrayList<ReturnPostDto> returnPostList = new ArrayList<>();
        for (PostEntity p : postFeed){
            ReturnPostDto returnPost = new ReturnPostDto();
            returnPost.setPostId(p.getId());
            returnPost.setComments(p.getComments());
            returnPost.setDescription(p.getDescription());
            returnPost.setPrimaryData(p.getPrimaryData());
            returnPost.setSecondaryData(p.getSecondaryData());
            returnPost.setImageUri(p.getImageUri());
            returnPost.setPostCreationTime(p.getCreatedAt());
            returnPost.setNumLikes(p.getLikes().size());
            returnPost.setNumComments(p.getComments().size());
            returnPost.setOriginalPosterId(p.getOriginalPosterID());
            returnPost.setOriginalPosterUsername(userRepository.findById(p.getOriginalPosterID()).get().getUsername());
            returnPostList.add(returnPost);
        }

        return new ResponseEntity<>(returnPostList, HttpStatus.OK);
    }
}
