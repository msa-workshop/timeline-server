package com.example.timelineserver.timeline;

import com.example.sns.common.PostInfo;

import java.time.ZonedDateTime;

public class SocialPost {

    private int postId;
    private String imageId;
    private String uploaderName;
    private int uploaderId;

    private ZonedDateTime uploadDatetime;
    private String contents;
    private Long likes;

    public SocialPost(PostInfo post, Long likes) {
        this(post.getPostId(), post.getImageId(), post.getUploaderName(), post.getUploaderId(), post.getUploadDatetime(), post.getContents(), likes == null ? 0 : likes);
    }

    public SocialPost(int postId, String imageId, String uploaderName, int uploaderId, ZonedDateTime uploadDatetime, String contents, Long likes) {
        this.postId = postId;
        this.imageId = imageId;
        this.uploaderName = uploaderName;
        this.uploaderId = uploaderId;
        this.uploadDatetime = uploadDatetime;
        this.contents = contents;
        this.likes = likes;
    }

    public int getPostId() {
        return postId;
    }

    public String getImageId() {
        return imageId;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public int getUploaderId() {
        return uploaderId;
    }

    public ZonedDateTime getUploadDatetime() {
        return uploadDatetime;
    }

    public String getContents() {
        return contents;
    }

    public Long getLikes() {
        return likes;
    }
}
