package com.example.course_service.exception;

import com.example.course_service.dto.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAny(Exception e, HttpServletRequest httpServletRequest) {

        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                500,
                LocalDateTime.now());

        log.error("Error: method={}, uri={}, message={}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                e.getMessage(),
                e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }



    @ExceptionHandler(CourseNotFound.class)
    public ResponseEntity<ErrorResponse> handleCourseNotFound(CourseNotFound e, HttpServletRequest httpServletRequest) {

        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                500,
                LocalDateTime.now());

        log.error("Error: method={}, uri={}, message={}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                e.getMessage(),
                e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }




}
