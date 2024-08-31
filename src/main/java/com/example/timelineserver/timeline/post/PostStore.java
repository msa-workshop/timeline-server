package com.example.timelineserver.timeline.post;

import com.example.sns.common.PostInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class PostStore {

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    public PostStore(StringRedisTemplate redis, ObjectMapper objectMapper) {
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    public void savePost(PostInfo post) {
        try {
            redis.opsForZSet().add("post:" + post.getUploaderId(), objectMapper.writeValueAsString(post), post.getUploadDatetime().toEpochSecond());
            redis.opsForZSet().add("post:all", objectMapper.writeValueAsString(post), post.getUploadDatetime().toEpochSecond());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public List<PostInfo> allFeed() {
        Set<String> savedFeed = redis.opsForZSet().reverseRange("post:all", 0, -1);
        return savedFeed.stream().map(feed -> {
            try {
                return objectMapper.readValue(feed, PostInfo.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    public List<PostInfo> listFeed(String userId) {
        Set<String> savedFeed = redis.opsForZSet().reverseRange("post:"+userId, 0, -1);
        return savedFeed.stream().map(feed -> {
            try {
                return objectMapper.readValue(feed, PostInfo.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    public Long likePost(int userId, int postId) {
        return redis.opsForSet().add("likes:" + postId, String.valueOf(userId));
    }

    public Long unlikePost(int userId, int postId) {
        return redis.opsForSet().remove("likes:" + postId, String.valueOf(userId));
    }

    public Boolean isLikePost(int userId, int postId) {
        return redis.opsForSet().isMember("likes:"+postId, String.valueOf(userId));
    }

    public Long countLikes(int postId) {
        return redis.opsForSet().size("likes:" + postId);
    }

    public Map<Integer, Long> countLikes(List<Integer> postIds) {
        Map<Integer, Long> likesMap = new HashMap<>();

        List<Object> results = redis.executePipelined((RedisCallback<Object>) connection -> {
            StringRedisConnection stringRedisConn = (StringRedisConnection) connection;

            for (int postId : postIds) {
                stringRedisConn.sCard("likes:" + postId);
            }
            return null;
        });

        int index = 0;
        for (int postId : postIds) {
            Long likeCount = (Long) results.get(index++); // Get the result from the results list
            likesMap.put(postId, likeCount);
        }

        return likesMap;
    }

}
