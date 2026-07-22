package com.example.file_service.exception;

import com.example.file_service.dto.ErrorResponse;
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


    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFileNotFound(FileNotFoundException e, HttpServletRequest httpServletRequest) {

        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                404,
                LocalDateTime.now());

        log.warn("File not found: method={}, uri={}, message={}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                e.getMessage(),
                e);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }


    @ExceptionHandler(UnsupportedFileTypeException.class)
    public ResponseEntity<ErrorResponse> handleUnsupported(UnsupportedFileTypeException e, HttpServletRequest httpServletRequest) {

        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                400,
                LocalDateTime.now());

        log.warn("File not found: method={}, uri={}, fileName={}, mimeType={}, message={}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                e.getFileName(),
                e.getMimeType(),
                e.getMessage(),
                e);


        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }


    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ErrorResponse> handleStorage(StorageException e, HttpServletRequest httpServletRequest) {

        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                500,
                LocalDateTime.now());

        log.error("Error with storage: method={}, uri={}, message={}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                e.getMessage(),
                e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }


    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSize(MaxUploadSizeExceededException e, HttpServletRequest httpServletRequest) {

        ErrorResponse errorResponse = new ErrorResponse(
                "File is too large",
                400,
                LocalDateTime.now());

        log.warn("File is too large: method={}, uri={}, message={}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                e.getMessage(),
                e);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }


    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ErrorResponse> handleMultipart(
            MultipartException e,
            HttpServletRequest request
    ) {

        log.warn("Multipart error: method={}, uri={}, message={}",
                request.getMethod(),
                request.getRequestURI(),
                e.getMessage(),
                e
        );

        ErrorResponse errorResponse = new ErrorResponse(
                "Invalid file upload",
                400,
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

}





//MultipartException