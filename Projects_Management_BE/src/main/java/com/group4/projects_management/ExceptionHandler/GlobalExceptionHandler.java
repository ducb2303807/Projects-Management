package com.group4.projects_management.ExceptionHandler;

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
        return BuildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request)
    {
        String errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return new ResponseEntity<>(new ErrorResponse("Dữ liệu không hợp lệ: " + errors), HttpStatus.BAD_REQUEST);
    }

    // lỗi Validation (400)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralError(Exception ex) {
        log.error("Lỗi hệ thống chưa xác định: ",ex);
        return BuildErrorResponse("Lỗi hệ thống! Vui lòng thử lại sau.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> BuildErrorResponse(String message, HttpStatus status) {
        ErrorResponse res = new ErrorResponse(
                status.value(),
                message,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(res, status);
    }
}
