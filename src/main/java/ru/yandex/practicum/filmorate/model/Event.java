package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Event {
    private Integer eventId;
    private Integer userId;
    private Integer entityId;
    private String eventType;
    private String operation;
    private Long timestamp;
}
