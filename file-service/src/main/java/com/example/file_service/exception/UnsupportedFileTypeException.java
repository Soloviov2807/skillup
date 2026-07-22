package com.example.file_service.exception;

public class UnsupportedFileTypeException extends RuntimeException {
    private final String fileName;
    private final String mimeType;

    public UnsupportedFileTypeException(String message, String fileName, String mimeType) {
        super(message);
        this.fileName = fileName;
        this.mimeType = mimeType;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMimeType() {
        return mimeType;
    }
}
