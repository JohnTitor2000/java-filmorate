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
        log.error("ID not found when trying to update.", e);
        return new ResponseEntity<>(Map.of("message", e.getMessage()),
                                    HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleGetNotFoundException(final GetNotFoundException e) {
        log.error("ID not found when trying to retrieve.", e);
        return new ResponseEntity<>(Map.of("message", e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleNotFoundException(final NotFoundException e) {
        log.error("ID not found.", e);
        return new ResponseEntity<>(Map.of("message", e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleValidationExceprion(final ValidationException e) {
        log.error("Validation error.", e);
        return new ResponseEntity<>(Map.of("message", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleValidationExceprion(final Error e) {
        log.error("Validation error.", e);
        return new ResponseEntity<>(Map.of("message", e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}