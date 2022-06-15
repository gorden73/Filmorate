package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Feed {
    private Long timestamp;
    @NonNull
    private Integer userId;
    private String eventType;
    private String operation;
    private Integer eventId;
    @NonNull
    private Integer entityId;

    public Feed(Integer userId, String eventType, String operation, Integer entityId) {
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.entityId = entityId;
        this.timestamp = Instant.now().getEpochSecond();
        this.eventId = null;
    }

    public Feed(Long timestamp, Integer userId, String eventType, String operation,
                Integer eventId, Integer entityId) {
        this.timestamp = timestamp;
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.eventId = eventId;
        this.entityId = entityId;
    }
}
