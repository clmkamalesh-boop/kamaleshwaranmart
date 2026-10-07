package com.kamaleshwaran.kamaleshwaranmart.dto;

import java.io.Serializable;

/**
 * Standard API Response envelope required for all /api/v1/... endpoints:
 * Success: { "success": true, "data": {...}, "error": null }
 * Error:   { "success": false, "data": null, "error": { "code": "...", "message": "..." } }
 */
public class ApiResponseDTO<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private T data;
    private ErrorDetail error;

    public ApiResponseDTO() {
    }

    public ApiResponseDTO(boolean success, T data, ErrorDetail error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    public static <T> ApiResponseDTO<T> success(T data) {
        return new ApiResponseDTO<>(true, data, null);
    }

    public static <T> ApiResponseDTO<T> error(String code, String message) {
        return new ApiResponseDTO<>(false, null, new ErrorDetail(code, message));
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ErrorDetail getError() {
        return error;
    }

    public void setError(ErrorDetail error) {
        this.error = error;
    }

    public static class ErrorDetail implements Serializable {
        private static final long serialVersionUID = 1L;

        private String code;
        private String message;

        public ErrorDetail() {
        }

        public ErrorDetail(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
