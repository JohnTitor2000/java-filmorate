package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private final Set<User> friends = new HashSet<>();
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