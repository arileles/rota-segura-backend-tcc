package br.com.furb.rotasegura.services.impl;

import br.com.furb.rotasegura.configurations.security.JwtService;
import br.com.furb.rotasegura.domain.entities.User;
import br.com.furb.rotasegura.domain.enumerators.Roles;
import br.com.furb.rotasegura.domain.records.UserLoginRecord;
import br.com.furb.rotasegura.domain.records.UserLoginResponseRecord;
import br.com.furb.rotasegura.domain.records.UserRegisterRecord;
import br.com.furb.rotasegura.domain.records.UserRegisterResponseRecord;
import br.com.furb.rotasegura.domain.utils.Utils;
import br.com.furb.rotasegura.infra.exception.ServiceException;
import br.com.furb.rotasegura.repositories.RoleRepository;
import br.com.furb.rotasegura.repositories.UserRepository;
import br.com.furb.rotasegura.services.AuthenticationService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtService jwtService;

    private static final Pattern BASIC_EMAIL_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PASSWORD_POLICY =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,}$"); // ≥8, pelo menos 1 letra e 1 dígito

    @Transactional
    public UserRegisterResponseRecord registerUser(@Valid UserRegisterRecord input) {
        if (input == null) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, "Payload de registro é obrigatório.");
        }
        final String name = Utils.safeTrim(input.name());
        final String rawEmail = Utils.safeTrim(input.email());
        final String password = input.password() == null ? "" : input.password();

        if (Utils.isBlank(name))         throw new ServiceException(HttpStatus.BAD_REQUEST, "Nome é obrigatório.");
        if (Utils.isBlank(rawEmail))     throw new ServiceException(HttpStatus.BAD_REQUEST, "E-mail é obrigatório.");
        if (Utils.isBlank(password))     throw new ServiceException(HttpStatus.BAD_REQUEST, "Senha é obrigatória.");

        final String email = rawEmail.toLowerCase(Locale.ROOT);
        if (!BASIC_EMAIL_REGEX.matcher(email).matches()) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, "E-mail inválido.");
        }
        if (name.length() < 2 || name.length() > 120) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, "Nome deve ter entre 2 e 120 caracteres.");
        }

        if (!PASSWORD_POLICY.matcher(password).matches()) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, "Senha fraca: mínimo 8 caracteres, com letras e números.");
        }
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ServiceException(HttpStatus.BAD_REQUEST ,"Já existe um usuário cadastrado com esse e-mail.");
        }

        var defaultRole = roleRepository.findByRole(Roles.USER);
        var userToSave = new User();
        userToSave.setId(UUID.randomUUID());
        userToSave.setName(name);
        userToSave.setEmail(email);
        userToSave.setIsActive(true);

        String encoded = new BCryptPasswordEncoder().encode(password);
        userToSave.setPassword(encoded);
        userToSave.setRoles(Set.of(defaultRole));
        var userSaved = userRepository.save(userToSave);
        return new UserRegisterResponseRecord(
                userSaved.getId(),
                userSaved.getName(),
                userSaved.getEmail()
        );
    }

    @Transactional
    @Override
    public UserLoginResponseRecord authenticate(UserLoginRecord input) {

        if (Objects.isNull(input.email()) || Objects.isNull(input.password())) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, "Os campos Email e Senha devem ser preenchidos");
        }

        authenticate(input.email(), input.password());

        var authenticatedUser =  userRepository.findByEmail(input.email()).orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        String jwtToken = jwtService.generateToken(createUserClaims(authenticatedUser), authenticatedUser);

        return new UserLoginResponseRecord(authenticatedUser.getId(), jwtToken, jwtService.getExpirationTime());
    }

    private void authenticate(String email, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (DisabledException e) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, "Esse Usuário está dasabilitado");
        } catch (LockedException e) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, "Esse Usuário está Bloqueado");
        } catch (BadCredentialsException | InternalAuthenticationServiceException e) {
            throw new ServiceException(HttpStatus.BAD_REQUEST, "Credenciais de Login Inválidas");
        }
    }

    private Map<String,Object> createUserClaims(User user){
        Map<String,Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("is_active", user.getIsActive());
        claims.put("roles", user.getRoles());
        return claims;
    }

}
