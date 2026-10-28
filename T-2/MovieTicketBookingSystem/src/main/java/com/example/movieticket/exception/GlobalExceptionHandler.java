package com.example.movieticket.exception;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError validation(MethodArgumentNotValidException ex) {
        List<String> messages = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage).distinct().toList();
        return new ApiError(Instant.now(), 400, "Validation Failed", messages);
    }
    @ExceptionHandler(TicketNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError notFound(TicketNotFoundException ex) { return error(404, "Ticket Not Found", ex.getMessage()); }
    @ExceptionHandler(DuplicateSeatException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError duplicate(DuplicateSeatException ex) { return error(409, "Duplicate Seat", ex.getMessage()); }
    @ExceptionHandler({HttpMessageNotReadableException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError badRequest(Exception ex) {
        String message = "Request body is invalid or contains an unknown field";
        return error(400, "Bad Request", message);
    }
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError conflict(IllegalStateException ex) { return error(409, "Conflict", ex.getMessage()); }
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError illegal(IllegalArgumentException ex) { return error(400, "Bad Request", ex.getMessage()); }
    private ApiError error(int status, String title, String message) {
        return new ApiError(Instant.now(), status, title, List.of(message));
    }
}
