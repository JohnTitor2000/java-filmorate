package ru.yandex.practicum.filmorate.exceptions;

public class NotPositiveFieldException extends RuntimeException{
    public NotPositiveFieldException(String message) {
        super(message);
    }
}
