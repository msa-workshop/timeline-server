package com.example.timelineserver.timeline;

import com.example.sns.common.PostInfo;
import com.example.timelineserver.timeline.post.PostStore;
import com.example.timelineserver.timeline.follow.FollowerStore;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TimelineService {

    private final PostStore postStore;
    private final FollowerStore followerStore;

    public TimelineService(PostStore postStore, FollowerStore followerStore) {
        this.postStore = postStore;
        this.followerStore = followerStore;
    }

    public List<SocialPost> listUserFeed(String userId) {
        List<PostInfo> feedList = postStore.listFeed(userId);
        Map<Integer, Long> likes = postStore.countLikes(feedList.stream().map(PostInfo::getPostId).toList());
        return feedList.stream().map(
                post -> new SocialPost(post, likes.getOrDefault(post.getPostId(), 0L))
        ).toList();
    }

    public List<SocialPost> getRandomPost(String userId, double randomPost) {
        List<SocialPost> myPost = userId.equals("none") ? List.of() : listMyFeed(userId);
        int randomPostSize = Math.max(10, (int)Math.ceil(myPost.size() * randomPost));
        List<SocialPost> allPost = new ArrayList<>(listAllFeed());

        Set<Integer> myPostIds = myPost.stream()
                .map(SocialPost::getPostId)
                .collect(Collectors.toSet());

        allPost.removeIf(post -> myPostIds.contains(post.getPostId()));

        List<SocialPost> picked;
        if (randomPostSize >= allPost.size()) {
            picked = allPost;
        } else {
            Collections.shuffle(allPost);
            picked = new ArrayList<>(allPost.subList(0, randomPostSize));
        }

        allPost.removeIf(post -> myPostIds.contains(post.getPostId()));

        return Stream.concat(myPost.stream(), picked.stream())
                .sorted(Comparator.comparing(SocialPost::getUploadDatetime).reversed())
                .collect(Collectors.toList());
    }

    public List<SocialPost> listFollowerFeed(Set<String> followerSet) {
        return followerSet
                .stream()
                .map(this::listUserFeed)
                .filter(Objects::nonNull) // Filter out any nulls that might have resulted from invalid ids
                .flatMap(List::stream) // Flatten the stream of lists into a stream of SocialPost
                .collect(Collectors.toList());
    }

    public List<SocialPost> listMyFeed(String userId) {
        Set<String> followers = followerStore.listFollower(String.valueOf(userId));
        List<SocialPost> myPost = listUserFeed(userId);
        List<SocialPost> followerFeed = listFollowerFeed(followers);

        return Stream.concat(myPost.stream(), followerFeed.stream())
                .sorted(Comparator.comparing(SocialPost::getUploadDatetime).reversed())
                .collect(Collectors.toList());
    }

    public List<SocialPost> listAllFeed() {
        List<PostInfo> feedList = postStore.allFeed();
        Map<Integer, Long> likes = postStore.countLikes(feedList.stream().map(PostInfo::getPostId).toList());
        return feedList.stream().map(
                post -> new SocialPost(post, likes.getOrDefault(post.getPostId(), 0L))
        ).toList();
    }

    public boolean likePost(int userId, int postId) {
        if (postStore.isLikePost(userId, postId)) {
            postStore.unlikePost(userId, postId);
            return false;
        } else {
            postStore.likePost(userId, postId);
            return true;
        }
    }

    public Long countLike(int postId) {
        return postStore.countLikes(postId);
    }

}
