package by.baykulbackend.services.user;

import by.baykulbackend.config.PasswordEncoderConfig;
import by.baykulbackend.database.dao.user.Profile;
import by.baykulbackend.database.dao.user.User;
import by.baykulbackend.database.model.Role;
import by.baykulbackend.database.repository.user.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final IUserRepository iUserRepository;
    private final PasswordEncoderConfig passwordEncoderConfig;

    public ResponseEntity<?> registerUser(User user) {
        Map<String, String> response = new HashMap<>();

        if (isValidNewUser(user, response)) {
            User newUser = new User();
            newUser.setLogin(user.getLogin());
            newUser.setEmail(user.getEmail());
            newUser.setPhoneNumber(user.getPhoneNumber());
            newUser.setRole(Role.USER);

            Profile profile = new Profile();
            profile.setUser(newUser);
            newUser.setProfile(profile);

            newUser.setPassword(passwordEncoderConfig.getPasswordEncoder().encode(user.getPassword()));

            iUserRepository.save(newUser);
            response.put("registration_user", "true");
            log.info("The user with id {} is registered. Login: {}", newUser.getId(), newUser.getLogin());
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(response);
    }

    private boolean isValidNewUser(User user, Map<String, String> response) {
        if (StringUtils.isBlank(user.getLogin())) {
            response.put("error_login", "The login must not be empty");
            log.warn("The login must not be empty");
            return false;
        }

        if (StringUtils.isBlank(user.getPassword())) {
            response.put("error_password", "The password must not be empty");
            log.warn("The password must not be empty");
            return false;
        }

        if (iUserRepository.findByLogin(user.getLogin()) != null) {
            response.put("error_login", "User with that login already exists");
            log.warn("User with login '{}' already exists", user.getLogin());
            return false;
        }

        if (iUserRepository.findByEmail(user.getEmail()) != null) {
            response.put("error_email", "User with that email already exists");
            log.warn("User with email '{}' already exists", user.getEmail());
            return false;
        }

        if (iUserRepository.findByPhoneNumber(user.getPhoneNumber()) != null) {
            response.put("error_phone_number", "User with that phone number already exists");
            log.warn("User with phone number '{}' already exists", user.getPhoneNumber());
            return false;
        }

        return true;
    }
}

