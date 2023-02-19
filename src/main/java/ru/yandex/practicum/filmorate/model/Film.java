package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private final Set<Integer> likes = new HashSet<>();
    private int id;
    @NotBlank
    @NotNull
    String name;
    @NotNull
    @Size(min = 1, max = 100)
    String description;
    @NotNull
    LocalDate releaseDate;
    @Positive
    Integer duration;

    public int getcountLikes() {
        return likes.size();
    }
}
