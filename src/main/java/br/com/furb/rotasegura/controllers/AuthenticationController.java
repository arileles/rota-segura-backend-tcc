package br.com.furb.rotasegura.controllers;

import br.com.furb.rotasegura.domain.records.UserLoginRecord;
import br.com.furb.rotasegura.domain.records.UserLoginResponseRecord;
import br.com.furb.rotasegura.domain.records.UserRegisterRecord;
import br.com.furb.rotasegura.domain.records.UserRegisterResponseRecord;
import br.com.furb.rotasegura.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/v1/auth")
@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<UserRegisterResponseRecord> register(@RequestBody UserRegisterRecord userRegisterRecord) {
        var registeredUser = authenticationService.registerUser(userRegisterRecord);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseRecord> authenticate(@RequestBody UserLoginRecord userLoginRecord) {
        var loginResponse = authenticationService.authenticate(userLoginRecord);
        return ResponseEntity.ok(loginResponse);
    }
}
