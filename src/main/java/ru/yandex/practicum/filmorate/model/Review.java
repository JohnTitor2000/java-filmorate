package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.*;

@Data
@Builder
public class Review {
    private Integer reviewId;
    @NotNull
    @NotBlank
    @Size(max = 500)
    private String content;
    @NotNull
    @JsonProperty(value = "isPositive")
    private Boolean isPositive;
    @NotNull
    @Positive
    private Integer userId;
    @NotNull
    @Positive
    private Integer filmId;
    private int useful;
}

