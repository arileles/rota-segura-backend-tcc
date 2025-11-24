package br.com.furb.rotasegura.exception;

import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends IOException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
