package com.example.timelineserver.timeline.post;

import com.example.sns.common.PostInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
public class PostListener {

    private final ObjectMapper objectMapper;
    private final PostStore postStore;

    public PostListener(ObjectMapper objectMapper, PostStore postStore) {
        this.objectMapper = objectMapper;
        this.postStore = postStore;
    }

    @KafkaListener(topics = "post.created", groupId = "timeline-server")
    public void listen(String message) {
        try {
            PostInfo post = objectMapper.readValue(message, PostInfo.class);
            postStore.savePost(post);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
