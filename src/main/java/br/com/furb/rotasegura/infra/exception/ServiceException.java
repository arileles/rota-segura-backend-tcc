package br.com.furb.rotasegura.infra.exception;

import org.springframework.http.HttpStatus;

public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private HttpStatus httpStatus;

    public ServiceException(HttpStatus errorCategory, String message) {
        super(message);
        this.httpStatus = errorCategory;
    }

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
}