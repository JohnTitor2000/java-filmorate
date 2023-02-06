package ru.yandex.practicum.filmorate.model;

import java.io.IOException;

public class ValidationException extends RuntimeException {
    public ValidationException(String m) {
        super(m);
    }
}
