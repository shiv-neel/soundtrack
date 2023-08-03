package com.SoundTrack.Backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name= "posts")
@Data
@NoArgsConstructor
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //Eager fetch type so all comments are loaded into memory the same time as the post.
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "post_comments", joinColumns = @JoinColumn(name = "post_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id", referencedColumnName = "id"))
    List<PostCommentEntity> comments = new ArrayList<>();

    //Eager fetch type so all likes are loaded into memory the same time as the post.
    //Doing this instead of an integer count of likes to maybe extend functionality later or to allow users to revoke their like
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "post_likes", joinColumns = @JoinColumn(name = "post_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "like_id", referencedColumnName = "id"))
    List<PostLikeEntity> likes = new ArrayList<>();

    //Saves time of construction
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private long originalPosterID;

    private String primaryData;
    private String secondaryData;
    private String imageUri;
    private String description;

    /*
    Not needed, opting for URLs instead
    @Lob
    private byte[] image;
    */

    public void addComment(PostCommentEntity commentEntity){
        comments.add(commentEntity);
    }

    public void addLike(PostLikeEntity like) { likes.add(like);}
    public void removeLike(long userId){
        for (PostLikeEntity l : likes){
            if (l.getLikeSenderId() == userId){
                likes.remove(l);
            }
        }
    }
    public boolean alreadyLikedByUser(long userId){
        for (PostLikeEntity l : likes){
            if (l.getLikeSenderId() == userId){
                return true;
            }
        }
        return false;
    }
}
