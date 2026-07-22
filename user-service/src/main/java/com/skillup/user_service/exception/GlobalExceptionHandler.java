package com.skillup.user_service.exception;

import com.skillup.user_service.model.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {






    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ) {


        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildResponse(errorMessage, HttpStatus.BAD_REQUEST, request);
    }




    // 🔐 Неверный логин / пароль
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException e,
            HttpServletRequest request
    ) {

        log.warn("Auth failed: uri={}, message={}",
                request.getRequestURI(),
                e.getMessage()
        );

        return buildResponse("Invalid username or password", HttpStatus.UNAUTHORIZED, request);
    }

    // 🔐 Нет доступа (JWT есть, но прав нет)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException e,
            HttpServletRequest request
    ) {

        log.warn("Access denied: uri={}", request.getRequestURI());

        return buildResponse("Access denied", HttpStatus.FORBIDDEN, request);
    }

    // 🔐 JWT ошибки (сделай своё исключение)
    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleJwt(
            JwtAuthenticationException e,
            HttpServletRequest request
    ) {

        log.warn("JWT error: uri={}, message={}",
                request.getRequestURI(),
                e.getMessage()
        );

        return buildResponse(e.getMessage(), HttpStatus.UNAUTHORIZED, request);
    }

    // 👤 Пользователь уже существует (регистрация)
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserExists(
            UserAlreadyExistsException e,
            HttpServletRequest request
    ) {

        log.warn("User exists: {}", e.getMessage());

        return buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST, request);
    }



    // 🔍 Пользователь не найден
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException e,
            HttpServletRequest request
    ) {

        log.warn("User not found: {}", e.getMessage());

        return buildResponse(e.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    // 💥 Всё остальное (fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(
            Exception e,
            HttpServletRequest request
    ) {

        log.error("Unexpected error: method={}, uri={}",
                request.getMethod(),
                request.getRequestURI(),
                e
        );

        return buildResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    // 🧩 helper метод (чтобы не дублировать код)
    private ResponseEntity<ErrorResponse> buildResponse(
            String message,
            HttpStatus status,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(
                        message,
                        status.value(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }
}