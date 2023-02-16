package ru.yandex.practicum.filmorate.exceptions;

public class UpdateNotFoundException extends NotFoundException{
    public UpdateNotFoundException(String message) {
        super(message);
    }
}
