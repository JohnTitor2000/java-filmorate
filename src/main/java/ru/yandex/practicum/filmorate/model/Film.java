package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;

@Setter
@Getter
@NoArgsConstructor
public class Film {
    private int id;
    @NotBlank
    @NotNull
    private String name;
    @Size(min = 1, max = 100)
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
    private Mpa mpa;
    ArrayList<Genre> genres = new ArrayList<>();
}