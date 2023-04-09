package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@FieldDefaults(level= AccessLevel.PRIVATE)
public class Review {
    Integer reviewId;
    @NotBlank
    @Size(max = 500)
    String content;
    @NotNull
    @JsonProperty(value = "isPositive")
    Boolean isPositive;
    @NotNull
    Integer userId;
    @NotNull
    Integer filmId;
    int useful;
}
