package ru.yandex.practicum.filmorate.exceptions;

public class GetNotFoundException extends NotFoundException{
    public GetNotFoundException(String message) {
        super(message);
    }
}
