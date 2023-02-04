package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class Film {
    private int id;
    @NotBlank
    @NotNull
    String name;
    @NotNull
    @Size(min = 1, max = 100)
    String description;
    @NotNull
    LocalDate releaseDate;
    Integer duration;
}
