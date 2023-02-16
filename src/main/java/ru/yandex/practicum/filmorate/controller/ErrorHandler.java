package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.exceptions.GetNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UpdateNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.util.Map;

@Slf4j
@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleUpdateNotFoundException(final UpdateNotFoundException e) {
        log.warn("ID not found when trying to update.");
        return new ResponseEntity<>(Map.of("Exeption", e.getMessage()),
                                    HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleGetNotFoundException(final GetNotFoundException e) {
        log.warn("ID not found when trying to retrieve.");
        return new ResponseEntity<>(Map.of("Exeption", e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleNotFoundException(final NotFoundException e) {
        log.warn("ID not found.");
        return new ResponseEntity<>(Map.of("Exeption", e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleValidationExceprion(final ValidationException e) {
        log.warn("Validation error.");
        return new ResponseEntity<>(Map.of("Exeption", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }
}