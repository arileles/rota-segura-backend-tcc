package br.com.furb.rotasegura.configurations;

import br.com.furb.rotasegura.infra.exception.ServiceException;
import br.com.furb.rotasegura.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException ex) {
        ErrorResponse error = new ErrorResponse(
            ex.getHttpStatus().value(),
            ex.getMessage()
        );
        return new ResponseEntity<>(error, ex.getHttpStatus());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(404, ex.getMessage());
        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(500, "Internal Server Error: " + ex.getMessage());
        return ResponseEntity.internalServerError().body(error);
    }

    private record ErrorResponse(int status, String message) {}
}