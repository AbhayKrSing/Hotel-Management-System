package com.airbnb.shared.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.airbnb.shared.dto.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	     @ExceptionHandler(MethodArgumentNotValidException.class)
	    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(
	            MethodArgumentNotValidException ex,
	            WebRequest request) {

	        Map<String, String> errors = new HashMap<>();
	        ex.getBindingResult().getAllErrors().forEach(error -> {
	            String fieldName = ((FieldError) error).getField();
	            String errorMessage = error.getDefaultMessage();
	            errors.put(fieldName, errorMessage);
	        });

	        ApiResponse<Void> response = ApiResponse.error(
	                HttpStatus.BAD_REQUEST.value(),
	                "Validation failed",
	                errors,
	                request.getDescription(false).replace("uri=", "")
	        );

	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	    }
	     
	     // ===== 404 - Resource Not Found =====
	     @ExceptionHandler(ResourceNotFoundException.class)
	     public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(
	             ResourceNotFoundException ex,
	             WebRequest request) {

	         ApiResponse<Void> response = ApiResponse.error(
	                 HttpStatus.NOT_FOUND.value(),
	                 ex.getMessage(),request.getDescription(false).replace("uri=", "")
	         );

	         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	     }
	     
	     // ===== 401 - Authentication Failed =====
	     @ExceptionHandler(UnauthorizedException.class)
	     public ResponseEntity<ApiResponse<Void>> handleUnauthorized(
	             UnauthorizedException ex,
	             WebRequest request) {

	         ApiResponse<Void> response = ApiResponse.error(
	                 HttpStatus.UNAUTHORIZED.value(),
	                 ex.getMessage(),
	                 request.getDescription(false).replace("uri=", "")
	         );
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	     }

	     // ===== 403 - Forbidden =====
	     @ExceptionHandler(ForbiddenException.class)
	     public ResponseEntity<ApiResponse<Void>> handleForbidden(
	             ForbiddenException ex,
	             WebRequest request) {

	         ApiResponse<Void> response = ApiResponse.error(
	                 HttpStatus.FORBIDDEN.value(),
	                 ex.getMessage(),
	                 request.getDescription(false).replace("uri=", "")
	         );

	         return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
	     }

	     // ===== 409 - Conflict =====
	     @ExceptionHandler(ConflictException.class)
	     public ResponseEntity<ApiResponse<Void>> handleConflict(
	             ConflictException ex,
	             WebRequest request) {

	         ApiResponse<Void> response = ApiResponse.error(
	                 HttpStatus.CONFLICT.value(),
	                 ex.getMessage(),
	                 request.getDescription(false).replace("uri=", "")
	         );

	         return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	     }

	     // ===== 404 - No Handler Found =====
	     @ExceptionHandler(NoHandlerFoundException.class)
	     public ResponseEntity<ApiResponse<Void>> handleNoHandlerFound(
	             NoHandlerFoundException ex,
	             WebRequest request) {

	         ApiResponse<Void> response = ApiResponse.error(
	                 HttpStatus.NOT_FOUND.value(),
	                 "Endpoint not found: " + ex.getRequestURL(),
	                 request.getDescription(false).replace("uri=", "")
	         );
	         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	     }

	     // ===== 500 - All Other Exceptions =====
	     @ExceptionHandler(Exception.class)
	     public ResponseEntity<ApiResponse<Void>> handleAllExceptions(
	             Exception ex,
	             WebRequest request) {

	         ApiResponse<Void> response = ApiResponse.error(
	                 HttpStatus.INTERNAL_SERVER_ERROR.value(),
	                 "An unexpected error occurred. Please try again later.",
	                 request.getDescription(false).replace("uri=", "")
	         );

	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	     }
}
