package by.baykulbackend.services.user;

import by.baykulbackend.config.PasswordEncoderConfig;
import by.baykulbackend.database.dao.user.User;
import by.baykulbackend.database.repository.user.IRefreshTokenRepository;
import by.baykulbackend.database.repository.user.IUserRepository;
import by.baykulbackend.exceptions.NotFoundException;
import by.baykulbackend.security.AuthService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final IUserRepository iUserRepository;
    private final IRefreshTokenRepository iRefreshTokenRepository;
    private final AuthService authService;
    private final PasswordEncoderConfig passwordEncoderConfig;

    public Optional<User> getByLogin(@NonNull String login) {
        User user = iUserRepository.findByLogin(login);

        if (user ==  null) {
            throw new UsernameNotFoundException("User not found");
        }

        return Optional.of(user);
    }

    public Optional<User> getByEmail(@NonNull String email) {
        User user = iUserRepository.findByEmail(email);

        if (user ==  null) {
            throw new UsernameNotFoundException("User not found");
        }

        return Optional.of(user);
    }

    public Optional<User> getByPhoneNumber(@NonNull String phoneNumber) {
        User user = iUserRepository.findByEmail(phoneNumber);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return Optional.of(user);
    }

    public User createUser(User user) {
        user.setCreatedTs(LocalDateTime.now());
        user.setUpdatedTs(LocalDateTime.now());
        user.setBlocked(false);
        iUserRepository.save(user);
        log.warn("User {} has ben created. -> {}", user.getLogin(),
                authService.getAuthInfo().getPrincipal());

        return user;
    }

    public ResponseEntity<?> deleteUserById(UUID id) {
        Map<Object, Object> response = new HashMap<>();
        User userFromDB = iUserRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));

        if (userFromDB == null) {
            response.put("error", "delete user");
            response.put("text", "User could not be deleted. User with id = " + id + " not found");
            log.warn("User could not be deleted. User with id = {} not found -> {}",
                    id, authService.getAuthInfo().getPrincipal());

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } else {
            iUserRepository.deleteById(id);
            response.put("delete_user", "true");
            log.info("Delete user with id = {} -> {}", id, authService.getAuthInfo().getPrincipal());
        }

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> updateUser(UUID id, User user) {
        Map<Object, Object> response = new HashMap<>();
        User userFromDB = iUserRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));

        if (userFromDB == null) {
            response.put("error", "update user");
            response.put("text", "User could not be update. User with id " + id + " not found");
            log.warn("User could not be updated. User with id {} not found -> {}",
                    id, authService.getAuthInfo().getPrincipal());

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (user.getLogin() != null) {
            userFromDB.setLogin(user.getLogin());
            log.info("User's login with id {} has been updated -> {}",
                    id, authService.getAuthInfo().getPrincipal());
        }

        if (user.getEmail() != null) {
            userFromDB.setEmail(user.getEmail());
            log.info("User's email with id {} has been updated -> {}",
                    id, authService.getAuthInfo().getPrincipal());
        }

        if (user.getPhoneNumber() != null) {
            userFromDB.setPhoneNumber(user.getEmail());
            log.info("User's phone number with id {} has been updated -> {}",
                    id, authService.getAuthInfo().getPrincipal());
        }

        if (user.getPassword() != null) {
            userFromDB.setPassword(passwordEncoderConfig.getPasswordEncoder().encode(user.getPassword()));
            iRefreshTokenRepository.deleteByUser(userFromDB);
            log.info("The password of User with the id {} has been updated -> {}",
                    id, authService.getAuthInfo().getPrincipal());
        }

        if (user.getRole() != null) {
            userFromDB.setRole(user.getRole());
            log.info("The role of User with the id {} has been updated -> {}",
                    id, authService.getAuthInfo().getPrincipal());
        }

        if (user.getBlocked() != null) {
            userFromDB.setBlocked(user.getBlocked());

            if (user.getBlocked()) {
                iRefreshTokenRepository.deleteByUser(userFromDB);
                log.info("The sessions of User with the id {} has been deleted -> {}",
                        id, authService.getAuthInfo().getPrincipal());
            }

            log.info("The blocked of User with the id {} has been updated -> {}",
                    id, authService.getAuthInfo().getPrincipal());
        }

        userFromDB.setUpdatedTs(LocalDateTime.now());
        iUserRepository.save(userFromDB);
        response.put("update_user", "true");

        return ResponseEntity.ok(response);
    }

    public List<User> searchUser(String text) {
        List<User> result = new ArrayList<>();
        result.addAll(iUserRepository.findByLoginContainingIgnoreCase(text));
        result.addAll(iUserRepository.findByEmailContainingIgnoreCase(text));
        result.addAll(iUserRepository.findByPhoneNumberContaining(text));

        return result.stream().distinct().collect(Collectors.toList());
    }
}
