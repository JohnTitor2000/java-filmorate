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

import javax.validation.ConstraintViolationException;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({NotFoundException.class, GetNotFoundException.class, UpdateNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFoundException(final Exception e) {
        log.error("ID not found.", e);
        return new ResponseEntity<>(Map.of("message", e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleValidationException(final ValidationException e) {
        log.error("Validation error.", e);
        return new ResponseEntity<>(Map.of("message", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleValidationException(final Error e) {
        log.error("Validation error.", e);
        return new ResponseEntity<>(Map.of("message", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleValidationException(final ConstraintViolationException e) {
        // Условный оператор необходим т.к. тесты в Postman требуют разные коды ответа
        // на невалидные поля класса Review: пустое поле - 500, отрицательное поле - 404
        if (e.getConstraintViolations().toString().contains("review.userId") &&
                e.getConstraintViolations().toString().contains("должно быть больше 0")) {
            log.error("Incorrect value - userId должно быть больше 0", e);
            return new ResponseEntity<>(Map.of("message", e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } else if (e.getConstraintViolations().toString().contains("review.filmId") &&
                e.getConstraintViolations().toString().contains("должно быть больше 0")) {
            log.error("Incorrect value - filmId должно быть больше 0", e);
            return new ResponseEntity<>(Map.of("message", e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } else {
            log.error("Incorrect value.", e);
            return new ResponseEntity<>(Map.of("message", e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}