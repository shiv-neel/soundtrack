package com.SoundTrack.Backend.controller;

import com.SoundTrack.Backend.dto.*;
import com.SoundTrack.Backend.model.PostCommentEntity;
import com.SoundTrack.Backend.model.PostEntity;
import com.SoundTrack.Backend.model.PostLikeEntity;
import com.SoundTrack.Backend.model.UserEntity;
import com.SoundTrack.Backend.repository.PostCommentEntityRepository;
import com.SoundTrack.Backend.repository.PostEntityRepository;
import com.SoundTrack.Backend.repository.PostLikeEntityRepository;
import com.SoundTrack.Backend.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/post/")
public class PostController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostEntityRepository postEntityRepository;
    @Autowired
    private PostCommentEntityRepository commentEntityRepository;
    @Autowired
    private PostLikeEntityRepository postLikeEntityRepository;

    @Operation(summary = "Method to create a new post by the given user",
            description = "This method accepts a data transfer object 'CreatePostDto' that is the standard for all posts to be created in the app. " +
                    "At a minimum, the post must provide a caption, or else the request will be rejected with status BAD_REQUEST. " +
                    "This method creates a new 'Post' object with the provided information, with the image in the form of a URL. (by Ian)")
    @PostMapping("create")
    @Secured("USER")
    public ResponseEntity<String> createUserPost(@RequestBody CreatePostDto postdto) {
        //Make sure a caption is present:
        if (postdto.getPrimaryData() == null) {
            return new ResponseEntity<>("No caption provided (minimum requirement)", HttpStatus.BAD_REQUEST);
        }
        //Get the byte array (image)
        /*
        Switched to using URI!!! Want to keep this for when we wanna use images for other posts
        try {
            imageInBytes = image.getBytes();
        } catch (IOException e){
            return new ResponseEntity<>("Error with image; IO exception occured while reading bytes from the image and storing in memory.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        */
        byte[] imageInBytes = null; //set as empty for now


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserEntity poster;

        try {
            poster = userRepository.findByUsername(userDetails.getUsername()).get();
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Database error: current user could not be loaded into memory.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //Create new post:
        PostEntity post = new PostEntity();
        post.setOriginalPosterID(poster.getId());
        //post.setImage(imageInBytes);
        post.setPrimaryData(postdto.getPrimaryData());
        post.setSecondaryData(postdto.getSecondaryData());
        post.setImageUri(postdto.getImageUri());
        post.setDescription(postdto.getDescription());
        //Add the post in memory then save to database
        poster.addPost(post);
        userRepository.save(poster);

        return new ResponseEntity<>("Success: user " + poster.getUsername() + " successfully created a new post.", HttpStatus.OK);
    }

    @PostMapping("wrappedPostTopArtists/{limit}")
    @Secured("USER")
    public ResponseEntity<String> wrappedPostTopArtists(@PathVariable int limit) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserEntity poster;
        ArrayList<String> artistNames = new ArrayList<>();

        try {
            poster = userRepository.findByUsername(userDetails.getUsername()).get();
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Current user could not be loaded into memory.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(poster.getSpotifyAccessToken()).build();

        final GetUsersTopArtistsRequest getUsersTopArtistsRequest = spotifyApi.getUsersTopArtists().limit(limit).build();


        try {
            final Paging<Artist> artistPaging = getUsersTopArtistsRequest.execute();
            for (Artist artist : artistPaging.getItems()) {
                artistNames.add(artist.getName());
            }
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        ImageGeneratorJsDto imageGeneratorJsDto = new ImageGeneratorJsDto();

        imageGeneratorJsDto.setItems(artistNames);
        imageGeneratorJsDto.setType("Artist");
        imageGeneratorJsDto.setUsername(poster.getUsername());
        //Call the microservice

        RestTemplate restTemplate = new RestTemplate();
        String endpoint = "http://localhost:3000/api/createCard";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.IMAGE_PNG));

        HttpEntity<ImageGeneratorJsDto> entity = new HttpEntity<>(imageGeneratorJsDto, headers);

        byte[] response = restTemplate.exchange(endpoint, HttpMethod.POST, entity, byte[].class).getBody();

        //save the response to the microservice
        String directoryPath = "/var/www/html/wrappedPosts/";
        //String directoryPath = "/home/ian/Pictures/test_soundtrack/"; for testing on local machine
        String filename = "wrapped_" + poster.getId() + "_" + poster.getUserPostsEverMade() + ".png";

        try {
            FileOutputStream fos = new FileOutputStream(directoryPath + filename);
            fos.write(response);
        } catch (FileNotFoundException e){
            return new ResponseEntity<>("Error: could not save post to server filesystem.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e){
            return new ResponseEntity<>("Error: could not write the image to the filesystem.", HttpStatus.INTERNAL_SERVER_ERROR);
        }



        //Create new post:
        PostEntity post = new PostEntity();
        post.setOriginalPosterID(poster.getId());
        //post.setImage(imageInBytes);
        post.setPrimaryData("My top " + limit + " artists this month!");
        String artists = String.join(", ", imageGeneratorJsDto.getItems());
        artists = artists.substring(0, artists.length() - 2);
        post.setSecondaryData(artists);
        post.setImageUri("http://coms-309-026.class.las.iastate.edu/wrappedPosts/" + filename);
        post.setDescription("Monthly wrapped post");
        //Add the post in memory then save to database
        poster.addPost(post);
        userRepository.save(poster);


        return new ResponseEntity<>("Success: user " + poster.getUsername() + " successfully created a new post with image: " + post.getImageUri(), HttpStatus.OK);
    }

    @PostMapping("wrappedPostTopTracks/{limit}")
    @Secured("USER")
    public ResponseEntity<String> wrappedPostTopTracks(@PathVariable int limit) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserEntity poster;
        ArrayList<String> trackNames = new ArrayList<>();

        try {
            poster = userRepository.findByUsername(userDetails.getUsername()).get();
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Error loading user into memory", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(poster.getSpotifyAccessToken()).build();

        final GetUsersTopTracksRequest getUsersTopTracksRequest = spotifyApi.getUsersTopTracks().limit(limit).build();


        try {
            final Paging<Track> trackPaging = getUsersTopTracksRequest.execute();
            String toAdd;
            for (Track track : trackPaging.getItems()) {
                toAdd = track.getName() + " - ";
                for (int i = 0; i < track.getArtists().length; i++){
                    ArtistSimplified a = track.getArtists()[i];
                    toAdd += a.getName();
                    if (track.getArtists().length > 1 && track.getArtists().length - 1 != i){
                        toAdd += ", ";
                    }
                }
                trackNames.add(toAdd);
            }
        } catch (Exception e) {
            return new ResponseEntity<String>("Invalid spotify user access token.", HttpStatus.BAD_REQUEST);
        }

        ImageGeneratorJsDto imageGeneratorJsDto = new ImageGeneratorJsDto();

        imageGeneratorJsDto.setItems(trackNames);
        imageGeneratorJsDto.setType("Track");
        imageGeneratorJsDto.setUsername(poster.getUsername());
        //Call the microservice

        RestTemplate restTemplate = new RestTemplate();
        String endpoint = "http://localhost:3000/api/createCard";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.IMAGE_PNG));

        HttpEntity<ImageGeneratorJsDto> entity = new HttpEntity<>(imageGeneratorJsDto, headers);

        byte[] response = restTemplate.exchange(endpoint, HttpMethod.POST, entity, byte[].class).getBody();

        //save the response to the microservice
        String directoryPath = "/var/www/html/wrappedPosts/";
        //String directoryPath = "/home/ian/Pictures/test_soundtrack/"; for testing on local machine
        String filename = "wrapped_" + poster.getId() + "_" + poster.getUserPostsEverMade() + ".png";

        try {
            FileOutputStream fos = new FileOutputStream(directoryPath + filename);
            fos.write(response);
        } catch (FileNotFoundException e){
            return new ResponseEntity<>("Error: could not save post to server filesystem.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e){
            return new ResponseEntity<>("Error: could not write the image to the filesystem.", HttpStatus.INTERNAL_SERVER_ERROR);
        }



        //Create new post:
        PostEntity post = new PostEntity();
        post.setOriginalPosterID(poster.getId());
        //post.setImage(imageInBytes);
        post.setPrimaryData("My top " + limit + " tracks this month!");
        String tracks = String.join(", ", imageGeneratorJsDto.getItems());
        tracks = tracks.substring(0, tracks.length() - 2);
        post.setSecondaryData(tracks);
        post.setImageUri("http://coms-309-026.class.las.iastate.edu/wrappedPosts/" + filename);
        post.setDescription("Monthly wrapped post");
        //Add the post in memory then save to database
        poster.addPost(post);
        userRepository.save(poster);


        return new ResponseEntity<>("Success: user " + poster.getUsername() + " successfully created a new post with image: " + post.getImageUri(), HttpStatus.OK);
    }
/*

            Save for debugging purposes

@GetMapping("getUsersTopArtists/{limit}")
    @Secured("USER")
    public ResponseEntity<WrappedPostDto> getUserTopArtists(@PathVariable int limit) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserEntity poster;
        ArrayList<String> artistNames = new ArrayList<>();

        try {
            poster = userRepository.findByUsername(userDetails.getUsername()).get();
        } catch (NoSuchElementException e) {
            return new ResponseEntity<WrappedPostDto>(new WrappedPostDto(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(poster.getSpotifyAccessToken()).build();

        final GetUsersTopArtistsRequest getUsersTopArtistsRequest = spotifyApi.getUsersTopArtists().limit(limit).build();


        try {
            final Paging<Artist> artistPaging = getUsersTopArtistsRequest.execute();
            for (Artist artist : artistPaging.getItems()) {
                artistNames.add(artist.getName());
            }
        } catch (Exception e) {
            return new ResponseEntity<WrappedPostDto>(new WrappedPostDto(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<WrappedPostDto>(new WrappedPostDto(), HttpStatus.OK); // how to pass data to response?
    }
 */



    @PostMapping("createWrapped")
    @Secured("USER")
    public ResponseEntity<String> createWrappedPost(@RequestBody WrappedPostDto postdto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserEntity poster;

        try {
            poster = userRepository.findByUsername(userDetails.getUsername()).get();
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Database error: current user could not be loaded into memory.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //Call the microservice

        RestTemplate restTemplate = new RestTemplate();
        String endpoint = "http://localhost:3000/api/createCard";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.IMAGE_PNG));

        ImageGeneratorJsDto imageGeneratorJsDto = new ImageGeneratorJsDto();
        imageGeneratorJsDto.setItems(postdto.getItems());
        imageGeneratorJsDto.setType(postdto.getType());
        imageGeneratorJsDto.setUsername(poster.getUsername());

        HttpEntity<ImageGeneratorJsDto> entity = new HttpEntity<>(imageGeneratorJsDto, headers);

        byte[] response = restTemplate.exchange(endpoint, HttpMethod.POST, entity, byte[].class).getBody();

        //save the response to the microservice
        String directoryPath = "/var/www/html/wrappedPosts/";
        //String directoryPath = "/home/ian/Pictures/test_soundtrack/"; for testing on local machine
        String filename = "wrapped_" + poster.getId() + "_" + poster.getUserPostsEverMade() + ".png";

        try {
            FileOutputStream fos = new FileOutputStream(directoryPath + filename);
            fos.write(response);
        } catch (FileNotFoundException e){
            return new ResponseEntity<>("Error: could not save post to server filesystem.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e){
            return new ResponseEntity<>("Error: could not write the image to the filesystem.", HttpStatus.INTERNAL_SERVER_ERROR);
        }



        //Create new post:
        PostEntity post = new PostEntity();
        post.setOriginalPosterID(poster.getId());
        //post.setImage(imageInBytes);
        post.setPrimaryData("My top five items this month!");
        post.setSecondaryData(postdto.getType());
        post.setImageUri("http://coms-309-026.class.las.iastate.edu/wrappedPosts/" + filename);
        post.setDescription("Monthly wrapped post");
        //Add the post in memory then save to database
        poster.addPost(post);
        userRepository.save(poster);


        return new ResponseEntity<>("Success: user " + poster.getUsername() + " successfully created a new post with post ID" + post.getId(), HttpStatus.OK);
    }

    @Operation(summary = "Method that provides all infomation necessary to render the given post ID on the client app.",
            description = "This method, if provided a valid 'postId', will return a ReturnPostDto JSON (see documentation below) " +
                    "that provides all information necessary for the client app to render the post in view. (by Ian)")
    @GetMapping("view/getPostById/{postId}")
    @Secured("USER")
    public ResponseEntity<ReturnPostDto> getPostById(@PathVariable long postId) {
        //check that post exists
        if (!postEntityRepository.existsById(postId)) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        //bring the post into memory
        PostEntity post;
        try {
            post = postEntityRepository.findById(postId).get();
        } catch (NoSuchElementException e) {
            //PostEntity cant load into memory
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        UserEntity originalPoster;
        try {
            originalPoster = userRepository.findById(post.getOriginalPosterID()).get();
        } catch (NoSuchElementException e) {
            //User cant load into memory
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //Create Dto to return post to frontend
        ReturnPostDto postJson = new ReturnPostDto();

        postJson.setPostId(postId);
        postJson.setPostCreationTime(post.getCreatedAt());
        postJson.setNumLikes(post.getLikes().size());
        postJson.setNumComments(post.getComments().size());
        //original poster details
        postJson.setOriginalPosterUsername(originalPoster.getUsername());
        postJson.setOriginalPosterId(originalPoster.getId());
        //stuff that was in the Dto
        postJson.setPrimaryData(post.getPrimaryData());
        postJson.setSecondaryData(post.getSecondaryData());
        postJson.setImageUri(post.getImageUri());
        postJson.setDescription(post.getDescription());
        //the list representation of comments
        postJson.setComments(post.getComments());

        //Now send that jawn over to the folks at android
        return new ResponseEntity<>(postJson, HttpStatus.OK);

    }
    @Operation(summary = "Method that returns the list of comments for a post based off its given postId",
            description = "When given a valid postId in the path variable, this method returns a list of all comments left on a given post " +
                    "in the form of a List of PostCommentEntities in JSON format. (by Ian)")
    @GetMapping("view/getCommentsForPostById/{postId}")
    @Secured("USER")
    public ResponseEntity<List<PostCommentEntity>> getCommentsForPostId(@PathVariable long postId){
        //check that post exists
        if (!postEntityRepository.existsById(postId)) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        //bring the post into memory
        PostEntity post;
        try {
            post = postEntityRepository.findById(postId).get();
        } catch (NoSuchElementException e) {
            //PostEntity cant load into memory
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(post.getComments(), HttpStatus.OK);
    }
    @Operation(summary = "Method to leave a comment on the given post",
            description = "Provided with valid information in the request body's PostAddCommentDto, this method makes the current authenticated " +
                    "user leave a comment under the desired post, also logging the time stamp of when the comment was left. (by Ian)")
    @PostMapping("comment/add")
    @Secured("USER")
    public ResponseEntity<String> addComment(@RequestBody PostAddCommentDto commentDto){

        //check that the post exists by ID
        PostEntity postEntity;
        try{
            postEntity = postEntityRepository.findById(commentDto.getPostId()).get();
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Failure: post with provided Post ID not found.", HttpStatus.BAD_REQUEST);
        }
        //check that the userID can be retrieved for the current user (not expected but need a try-catch block)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        long originalPosterId;
        try{
            originalPosterId = userRepository.findByUsername(userDetails.getUsername()).get().getId();
        } catch (NoSuchElementException e){
            return new ResponseEntity<>("Error loading the userId from current user details (Server-side error)", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //create the comment entity
        PostCommentEntity commentEntity = new PostCommentEntity();
        commentEntity.setCommentText(commentDto.getComment());
        commentEntity.setCommenterUserID(originalPosterId);
        commentEntity.setPostId(postEntity.getId());
        //add to the PostEntity in memory
        postEntity.addComment(commentEntity);
        postEntityRepository.save(postEntity);
        //indicate success
        return new ResponseEntity<>("Success: created comment", HttpStatus.OK);
    }

    @Operation(summary = "Method to delete a comment by a comment ID",
            description = "Provided with a valid commentId in the form of a Long in JSON format, if the comment was made by the authenticated " +
                    "user making the request, the comment is deleted. (by Ian)")
    @DeleteMapping("comment/deleteCommentById")
    @Secured("USER")
    public ResponseEntity<String> deleteCommentById(@RequestBody LongDto longDto){
        long commentId = longDto.getLongInput();
        //load commentEntity into memory
        PostCommentEntity comment;
        try {
            comment = commentEntityRepository.findById(commentId).get();
        } catch (NoSuchElementException e){
            return new ResponseEntity<>("Error: comment with the provided ID could not be found.", HttpStatus.BAD_REQUEST);
        }
        //bring the post into memory
        PostEntity post;
        try {
            post = postEntityRepository.findById(comment.getPostId()).get();
        } catch (NoSuchElementException e) {
            //PostEntity cant load into memory
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //load userId
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        long originalCommenterId;
        try{
            originalCommenterId = userRepository.findByUsername(userDetails.getUsername()).get().getId();
        } catch (NoSuchElementException e){
            return new ResponseEntity<>("Error loading the userId from current user details (Server-side error)", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //Check if the target comment is owned by the user
        if (originalCommenterId != comment.getCommenterUserID()){
            return new ResponseEntity<>("Fail: Cannot delete a comment that is not yours", HttpStatus.UNAUTHORIZED);
        }
        //Delete the comment
        List<PostCommentEntity> postCommentsList= post.getComments();
        if (postCommentsList.remove(comment)){
            postEntityRepository.save(post);
            return new ResponseEntity<>("Success: comment deleted successfully.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Fail: PostEntity object failed to remove the comment.", HttpStatus.BAD_REQUEST);
        }
    }
    @Operation(summary = "Add a like to the given post",
            description = "This method has the authenticated user add a like to the provided postId. The post can only be liked once, " +
                    "and if the user tries to like twice, they will receive a message stating they cant like the post twice. (by Ian)")
    @PostMapping("like/add/{postId}")
    @Secured("USER")
    public ResponseEntity<String> addLike(@PathVariable long postId){

        //check that the post exists by ID
        PostEntity postEntity;
        try{
            postEntity = postEntityRepository.findById(postId).get();
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Failure: post with provided Post ID not found.", HttpStatus.BAD_REQUEST);
        }
        //check that the userID can be retrieved for the current user (not expected but need a try-catch block)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        long likerId;
        try{
            likerId = userRepository.findByUsername(userDetails.getUsername()).get().getId();
        } catch (NoSuchElementException e){
            return new ResponseEntity<>("Error loading the userId from current user details (Server-side error)", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //check if already liked
        if (postEntity.alreadyLikedByUser(likerId)){
            return new ResponseEntity<>("Post has already been liked by user.", HttpStatus.OK);
        }
        //create the like entity
        PostLikeEntity like = new PostLikeEntity();
        like.setPostLikedId(postId);
        like.setLikeSenderId(likerId);
        //add to the PostEntity in memory
        postEntity.addLike(like);
        postEntityRepository.save(postEntity);
        //indicate success
        return new ResponseEntity<>("Success: user liked post.", HttpStatus.OK);
    }
    @Operation(summary = "Removed the like for a given post",
            description = "Given a valid postId, if the authenticated user currently has a like left on the post, this method removes the like. (by Ian)")
    @DeleteMapping("like/remove/{postId}")
    @Secured("USER")
    public ResponseEntity<String> removeLike(@PathVariable long postId){

        //check that the post exists by ID
        PostEntity postEntity;
        try{
            postEntity = postEntityRepository.findById(postId).get();
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Failure: post with provided Post ID not found.", HttpStatus.BAD_REQUEST);
        }
        //check that the userID can be retrieved for the current user (not expected but need a try-catch block)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        long likerId;
        try{
            likerId = userRepository.findByUsername(userDetails.getUsername()).get().getId();
        } catch (NoSuchElementException e){
            return new ResponseEntity<>("Error loading the userId from current user details (Server-side error)", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //check if already liked
        if (!(postEntity.alreadyLikedByUser(likerId))){
            return new ResponseEntity<>("Post has not been liked by user.", HttpStatus.OK);
        }
        //load the like entity into memory
        PostLikeEntity like;
        try {
            like = postLikeEntityRepository.findByPostLikedId(postId).get();
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Error loading the post like into memory (Server-side error)", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //remove the like entity
        //Delete the comment
        List<PostLikeEntity> postLikesList = postEntity.getLikes();
        if (postLikesList.remove(like)){
            postEntityRepository.save(postEntity);
            return new ResponseEntity<>("Success: user disliked post.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Fail: PostEntity object failed to remove the comment.", HttpStatus.BAD_REQUEST);
        }

    }


    /*
        OUTDATED - keeping for reference when we reintroduce LOB's to posts

        PLS DO NOT DELETE this took me awhile to figure out lol. We will also need it for future features
            -Ian Dalton

    //View posts of a user:
    @PostMapping("view/{targetUser}")
    @Secured({"USER", "ADMIN", "CURATOR"})
    public ResponseEntity<MultiValueMap<String, Object>> viewUserPosts(@PathVariable String targetUser) {
        //check target user exists
        if (!userRepository.existsByUsername(targetUser)){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        //bring target user into memory
        UserEntity target;
        try {
            target = userRepository.findByUsername(targetUser).get();
        } catch (NoSuchElementException e){
            //User cant load into memory
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        List<PostEntity> posts = target.getUserPosts();


        //Create a multipart body, one part for each post.
        //First part of a post is a JSON with post data (poster, likes, etc)
        //Second part is the image.
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();

        for (PostEntity post: posts){
            //Create a DTO for JSON data transfer:
            ReturnPostDto postJson = new ReturnPostDto();
            postJson.setPostCreationTime(post.getCreatedAt());
            postJson.setNumLikes(post.getLikes().size());
            postJson.setNumComments(post.getComments().size());
            postJson.setOriginalPoster(targetUser);
            postJson.setCaption(post.getPrimaryData());
            postJson.setComments(post.getComments());
            //Add wrapper for image byte
            ByteArrayResource wrappedImage = new ByteArrayResource(post.getImage());
            HttpHeaders imageHeaders = new HttpHeaders();
            imageHeaders.setContentType(MediaType.IMAGE_JPEG);
            //Now combine into 'parts' MultiValueMap:
            parts.add("postJson", new HttpEntity<>(postJson));
            parts.add("image", new HttpEntity<>(wrappedImage, imageHeaders));
        }

        //Allows you to set response headers. Pretty neat!
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA); //specify the response. We are doing mixed.

        //Now send that jawn over to the folks at android!
        return new ResponseEntity<>(parts, headers, HttpStatus.OK);

    }
     */
}
