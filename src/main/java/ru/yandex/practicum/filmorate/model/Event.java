package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    Integer eventId;
    Integer userId;
    Integer entityId;
    String eventType;
    String operation;
    Long timestamp;
}
