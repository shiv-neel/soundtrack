package com.SoundTrack.Backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Entity
@Table(name= "users")
@Getter
@Setter
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;
    private String email;
    private String fullName;
    private String password;
    private String spotifyUsername;
    private String spotifyAccessToken;

    //110 x 110 JPG, pls :^)
    private String profilePicture;

    private int userPostsEverMade = 0;


    /*
            Relationship is defined in the UserEntity class because we want the roles to be pulled with the user, not vice versa.
            AKA, we do NOT want many to many in the Role class
     */
    @ManyToMany(fetch = FetchType.EAGER) //When you load a user, you ALWAYS want your roles to be shown
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    @OnDelete(action = OnDeleteAction.NO_ACTION) //Allows us to delete a UserEntity and its respective entry in 'user_roles' without deleting entries from the 'roles' table -Ian
    private List<Role> roles = new ArrayList<>();
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_friend_requests", joinColumns = @JoinColumn(name = "user_sender_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "friend_request_id", referencedColumnName = "id"))
    private List<FriendRequest> friendRequests = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "user_posts", joinColumns = @JoinColumn(name = "user_poster_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "post_id", referencedColumnName = "id"))
    private List<PostEntity> userPosts = new ArrayList<>();

    //Below is for future use. It is mad because it doesn't want to use integer for the container
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_friends", joinColumns = @JoinColumn(name = "friend_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "friendship_id", referencedColumnName = "id"))
    private List<Friendship> friends = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_followers", joinColumns = @JoinColumn(name = "follower_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "followerRelationship_id", referencedColumnName = "id"))
    private List<FollowerRelationship> followers = new ArrayList<>(); //held by curators to see who is following them

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "curators_following", joinColumns = @JoinColumn(name = "curator_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "followingRelationship_id", referencedColumnName = "id"))
    private List<FollowingRelationship> followings = new ArrayList<>(); //held by basic Users that are following curators

    public void addFriendRequest(FriendRequest request){
        friendRequests.add(request);
    }

    public FriendRequest findFriendRequest(UserEntity sender, UserEntity receiver) {
        for (FriendRequest f: friendRequests){
            //if a friend request exists where the sender or the receiver matches with the "otherUser" tag, delete that request.
            if (f.getSender().getUsername().equals(sender.getUsername()) && f.getReceiver().getUsername().equals(receiver.getUsername())){
                return f;
            }
        }
        return null;
    }

    public List<FriendRequest> getFriendRequests(UserEntity receiver) {
        List<FriendRequest> currentRequests = new ArrayList<>();
        for (FriendRequest f: friendRequests){
            if (f.getReceiver().getUsername().equals(receiver.getUsername())){
                currentRequests.add(f);
            }
        }
        return currentRequests;
    }

    public void removeFriendRequest(FriendRequest friendRequest) {
        for (Iterator<FriendRequest> iterator = friendRequests.iterator(); iterator.hasNext();) {
            FriendRequest request = iterator.next();
            if (request.equals(friendRequest)) {
                iterator.remove();
                request.setReceiver(null);
                request.setSender(null);
            }
        }
    }

    public Friendship findFriend(UserEntity otherUser){
        for (Friendship f: friends){
            //if a friendship exists where the friendUser matches the otherUser, return that friendship.
            if (f.getFriendUser().getUsername().equals(otherUser.getUsername())){
                return f;
            }
        }
        return null;
    }

    public void removeFriend(Friendship friend) {
        for (Iterator<Friendship> iterator = friends.iterator(); iterator.hasNext();) {
            Friendship friendship = iterator.next();
            if (friendship.equals(friend)) {
                iterator.remove();
                friendship.setCurrentUser(null);
                friendship.setFriendUser(null);
            }
        }
    }

    public FollowingRelationship findFollowing(UserEntity otherUser){
        for (FollowingRelationship f: followings){
            //if a following exists where the curatorUser matches the otherUser, return that following.
            if (f.getCuratorUser().getUsername().equals(otherUser.getUsername())){
                return f;
            }
        }
        return null;
    }

    public void removeFollowing(FollowingRelationship followingRelationship) {
        for (Iterator<FollowingRelationship> iterator = followings.iterator(); iterator.hasNext();) {
            FollowingRelationship following = iterator.next();
            if (following.equals(followingRelationship)) {
                iterator.remove();
                following.setCurrentUser(null);
                following.setCuratorUser(null);
            }
        }
    }

    public FollowerRelationship findFollower(UserEntity otherUser){
        for (FollowerRelationship f: followers){
            //if a follower exists where the followerUser matches the otherUser, return that follower.
            if (f.getFollowerUser().getUsername().equals(otherUser.getUsername())){
                return f;
            }
        }
        return null;
    }

    public void removeFollower(FollowerRelationship followerRelationship) {
        for (Iterator<FollowerRelationship> iterator = followers.iterator(); iterator.hasNext();) {
            FollowerRelationship follower = iterator.next();
            if (follower.equals(followerRelationship)) {
                iterator.remove();
                follower.setFollowerUser(null);
                follower.setCurrentCurator(null);
            }
        }
    }

    public boolean hasPendingFriendRequest(String from){
        for (FriendRequest f: friendRequests){
            if (f.getSender().getUsername().equals(from)){ return true;}
        }
        return false;
    }

    public void addPost(PostEntity post){

        userPosts.add(post);
        this.userPostsEverMade++;
    }
    public List<PostEntity> getUserPosts(){
        return this.userPosts;
    }

    public void addFriend(Friendship friendship){
        friends.add(friendship);
    }

    public List<Friendship> getFriends(){
        List<Friendship> userFriends = new ArrayList<>();
        for (Friendship f: friends){
            userFriends.add(f);
        }
        return userFriends;
    }

    public void addFollower(FollowerRelationship followerRelationship) {
        followers.add(followerRelationship);
    }

    public List<FollowerRelationship> getFollowers(){
        List<FollowerRelationship> userFollowers = new ArrayList<>();
        for (FollowerRelationship f: followers){
            userFollowers.add(f);
        }
        return userFollowers;
    }

    public void addFollowing(FollowingRelationship followingRelationship) {
        followings.add(followingRelationship);
    }

    public List<FollowingRelationship> getFollowing(){
        List<FollowingRelationship> userFollowings = new ArrayList<>();
        for (FollowingRelationship f: followings){
            userFollowings.add(f);
        }
        return userFollowings;
    }

    public List<Long> getPostsAsIds(){
        List<Long> returnList = new ArrayList<Long>();
        for (PostEntity p: userPosts){
            returnList.add(p.getId());
        }
        return returnList;
    }

//    public void removeAllRoles(){
//        for (Iterator<Role> iterator = roles.iterator(); iterator.hasNext();) {
//            Role role = iterator.next();
//            role.setName(null);
//        }
//    }

    public void removeAllFriendRequests(){
        for (Iterator<FriendRequest> iterator = friendRequests.iterator(); iterator.hasNext();) {
            FriendRequest request = iterator.next();
            request.setReceiver(null);
            request.setSender(null);
        }
    }

    public void removeAllUserPosts(){
        for (Iterator<PostEntity> iterator = userPosts.iterator(); iterator.hasNext();) {
            PostEntity post = iterator.next();
            post.setDescription(null);
            post.setComments(null);
            post.setImageUri(null);
            post.setPrimaryData(null);
            post.setSecondaryData(null);
            post.setCreatedAt(null);
            post.setLikes(null);
        }
    }

    public void removeAllFriends(){
        for (Iterator<Friendship> iterator = friends.iterator(); iterator.hasNext();) {
            Friendship friendship = iterator.next();
            UserEntity otherUser = friendship.getFriendUser();
            otherUser.removeFriend(otherUser.findFriend(friendship.getCurrentUser()));
            friendship.setFriendUser(null);
            friendship.setCurrentUser(null);
        }
    }

    public void removeAllFollowers(){
        for (Iterator<FollowerRelationship> iterator = followers.iterator(); iterator.hasNext();) {
            FollowerRelationship followerRelationship = iterator.next();
            UserEntity otherUser = followerRelationship.getFollowerUser();
            otherUser.removeFollowing(otherUser.findFollowing(followerRelationship.getCurrentCurator()));
            followerRelationship.setFollowerUser(null);
            followerRelationship.setCurrentCurator(null);
        }
    }

    public void removeAllFollowings(){
        for (Iterator<FollowingRelationship> iterator = followings.iterator(); iterator.hasNext();) {
            FollowingRelationship followingRelationship = iterator.next();
            UserEntity otherUser = followingRelationship.getCuratorUser();
            otherUser.removeFollower(otherUser.findFollower(followingRelationship.getCurrentUser()));
            followingRelationship.setCuratorUser(null);
            followingRelationship.setCurrentUser(null);
        }
    }

}
