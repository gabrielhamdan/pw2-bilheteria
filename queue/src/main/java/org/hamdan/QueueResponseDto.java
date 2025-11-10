package org.hamdan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record QueueResponseDto(String message, String sessionId, EQueueStatus status, String token) {

    public QueueResponseDto(String message) {
        this(message, null, null, null);
    }

    public QueueResponseDto(String message, String sessionId, EQueueStatus status) {
        this(message, sessionId, status, null);
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

}
