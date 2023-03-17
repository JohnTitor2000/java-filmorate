package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Data
public class User {

    int id;
    @NotNull
    @Email
    String email;
    @NotNull
    @NotBlank
    String login;
    String name;
    @NotNull
    @PastOrPresent(message = "The birthday cannot be in the future tense.")
    LocalDate birthday;
}