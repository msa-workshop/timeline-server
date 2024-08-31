package com.example.sns.common;

import java.time.ZonedDateTime;

public class PostInfo {

    private int postId;
    private String imageId;
    private int uploaderId;
    private String uploaderName;

    private ZonedDateTime uploadDatetime;
    private String contents;

    public PostInfo() {
    }

    public PostInfo(int postId, String imageId, int uploaderId, String uploaderName, ZonedDateTime uploadDatetime, String contents) {
        this.postId = postId;
        this.imageId = imageId;
        this.uploaderId = uploaderId;
        this.uploaderName = uploaderName;
        this.uploadDatetime = uploadDatetime;
        this.contents = contents;
    }

    public int getPostId() {
        return postId;
    }

    public String getImageId() {
        return imageId;
    }

    public int getUploaderId() {
        return uploaderId;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public ZonedDateTime getUploadDatetime() {
        return uploadDatetime;
    }

    public String getContents() {
        return contents;
    }
}