package br.com.furb.rotasegura.services;

import br.com.furb.rotasegura.domain.records.UserLoginRecord;
import br.com.furb.rotasegura.domain.records.UserLoginResponseRecord;
import br.com.furb.rotasegura.domain.records.UserRegisterRecord;
import br.com.furb.rotasegura.domain.records.UserRegisterResponseRecord;

public interface AuthenticationService {

    UserRegisterResponseRecord registerUser(UserRegisterRecord input);

    UserLoginResponseRecord authenticate(UserLoginRecord input);
}
