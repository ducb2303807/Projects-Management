package com.group4.projects_management.exceptionHandler;

import com.group4.common.enums.BusinessErrorCode;
import com.group4.projects_management.core.exception.BusinessException;
import com.group4.projects_management.core.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.group4.common.dto.ErrorResponse;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

//xử lý ngoại lệ toàn cục cho các API REST
@RestControllerAdvice
// ghi nhật ký
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // lỗi Not Found 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(ResourceNotFoundException ex) {
        log.error(ex.getMessage());
        return buildErrorResponse(ex.getMessage(), BusinessErrorCode.SYSTEM_RESOURCE_NOT_FOUND.getCode(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        log.error(ex.getMessage());
        return buildErrorResponse(ex.getMessage(), ex.getErrorCode().getCode(), HttpStatus.BAD_REQUEST);
    }

    // lỗi runtime
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        return buildErrorResponse(ex.getMessage(), ex.getClass().getSimpleName(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // lỗi Validation (400)
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        String errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return (ResponseEntity) buildErrorResponse(
                "Dữ liệu không hợp lệ: " + errors,
                BusinessErrorCode.SYSTEM_VALIDATION_ERROR.getCode(),
                HttpStatus.BAD_REQUEST
        );
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralError(Exception ex) {
        log.error("Lỗi hệ thống chưa xác định: ", ex);
        return buildErrorResponse("Lỗi hệ thống! Vui lòng thử lại sau.", BusinessErrorCode.SYSTEM_INTERNAL_SERVER_ERROR.getCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, String errorCode, HttpStatus status) {
        return new ResponseEntity<>(createErrorBody(status.value(), errorCode, message), status);
    }

    private ErrorResponse createErrorBody(int status, String errorCode, String message) {
        return new ErrorResponse(
                status,
                errorCode,
                message,
                LocalDateTime.now()
        );
    }
}
