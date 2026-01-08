package by.baykulbackend.services.user;

import by.baykulbackend.database.dao.user.RefreshToken;
import by.baykulbackend.database.dao.user.User;
import by.baykulbackend.database.model.Role;
import by.baykulbackend.database.repository.user.IRefreshTokenRepository;
import by.baykulbackend.database.repository.user.IUserRepository;
import by.baykulbackend.exceptions.NotFoundException;
import by.baykulbackend.security.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenService {
    private final IRefreshTokenRepository iRefreshTokenRepository;
    private final AuthService authService;
    private final IUserRepository iUserRepository;

    public List<RefreshToken> findUserRefreshTokens() {
        User userFromDB = iUserRepository.findByLogin(authService.getAuthInfo().getPrincipal().toString());

        return iRefreshTokenRepository.findRefreshTokenByUser(userFromDB);
    }

    public List<RefreshToken> findUserRefTokensByUserId(UUID id) {
        User userFromDB = iUserRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));

        return iRefreshTokenRepository.findRefreshTokenByUser(userFromDB);
    }

    public ResponseEntity<?> deleteById(UUID id) {
        Map<Object, Object> response = new HashMap<>();
        RefreshToken refreshTokenFromDB = iRefreshTokenRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Refresh token not found"));
        User userFromDB = iUserRepository.findByLogin(authService.getAuthInfo().getPrincipal().toString());

        if (userFromDB.equals(refreshTokenFromDB.getUser()) || userFromDB.getRole().equals(Role.ADMIN)) {
            iRefreshTokenRepository.deleteById(id);
            response.put("delete_refresh_token", "true");
            log.info("Delete refresh token with id = {} -> {}", id, authService.getAuthInfo().getPrincipal());

            return ResponseEntity.ok(response);
        }

        response.put("delete_refresh_token", "false");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
