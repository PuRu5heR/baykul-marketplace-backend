package by.baykulbackend.security;

import by.baykulbackend.database.dao.user.RefreshToken;
import by.baykulbackend.database.dao.user.User;
import by.baykulbackend.database.model.JwtAuthentication;
import by.baykulbackend.database.repository.user.IRefreshTokenRepository;
import by.baykulbackend.database.repository.user.IUserRepository;
import by.baykulbackend.exceptions.JwtAuthenticationException;
import by.baykulbackend.services.user.RequestService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final IRefreshTokenRepository iRefreshTokenRepository;
    private final IUserRepository iUserRepository;
    private final RequestService requestService;

    public JwtResponse login(String userAgent, HttpServletRequest request, @NonNull JwtRequest authRequest) {
        final User user = Optional.of(iUserRepository.findByLogin(authRequest.getLogin()))
                .orElseThrow(() -> new JwtAuthenticationException("User not found", HttpStatus.FORBIDDEN));

        if (user.getBlocked()) {
            log.warn("The user = {} with id = {} is blocked. Authorization failed", user.getLogin(), user.getId());
            throw new JwtAuthenticationException("The user " + user.getLogin() + "is blocked. Authorization failed",
                    HttpStatus.FORBIDDEN);
        }

        if (passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            final String accessToken = jwtProvider.generateAccessToken(user);
            final String refreshToken = jwtProvider.generateRefreshToken(user);
            String clientIp = requestService.getClientIp(request);
            RefreshToken refToken = new RefreshToken();
            refToken.setUser(user);
            refToken.setName(refreshToken);
            refToken.setUserAgent(userAgent);
            refToken.setIpAddress(clientIp);
            user.setLastTimeOnline(LocalDateTime.now());
            iUserRepository.save(user);
            iRefreshTokenRepository.save(refToken);

            return new JwtResponse(accessToken, refreshToken);
        } else {
            log.warn("Invalid password. User = {} with id = {}", user.getLogin(), user.getId());
            throw new JwtAuthenticationException("Invalid password", HttpStatus.FORBIDDEN);
        }
    }

    public JwtResponse getAccessToken(@NonNull String refreshToken, HttpServletRequest request) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = iRefreshTokenRepository.findRefreshTokenByName(refreshToken).getName();

            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final User user = Optional.of(iUserRepository.findByLogin(login))
                        .orElseThrow(() -> new JwtAuthenticationException("User not found", HttpStatus.FORBIDDEN));
                final String accessToken = jwtProvider.generateAccessToken(user);
                String clientIp = requestService.getClientIp(request);
                RefreshToken refreshTokenFromDb = iRefreshTokenRepository.findRefreshTokenByName(refreshToken);
                refreshTokenFromDb.setIpAddress(clientIp);
                iRefreshTokenRepository.save(refreshTokenFromDb);
                user.setLastTimeOnline(LocalDateTime.now());
                iUserRepository.save(user);

                return new JwtResponse(accessToken, null);
            }
        }

        return new JwtResponse(null, null);
    }

    public JwtResponse refresh(@NonNull String refreshToken, HttpServletRequest request) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final RefreshToken refreshTokenFromDb = iRefreshTokenRepository.findRefreshTokenByName(refreshToken);

            if (refreshTokenFromDb.getName() != null && refreshTokenFromDb.getName().equals(refreshToken)) {
                final User user = Optional.of(iUserRepository.findByLogin(login))
                        .orElseThrow(() -> new JwtAuthenticationException("User not found", HttpStatus.FORBIDDEN));
                final String newAccessToken = jwtProvider.generateAccessToken(user);
                final String newRefreshToken = jwtProvider.generateRefreshToken(user);
                String clientIp = requestService.getClientIp(request);
                refreshTokenFromDb.setName(newRefreshToken);
                refreshTokenFromDb.setIpAddress(clientIp);
                iRefreshTokenRepository.save(refreshTokenFromDb);

                return new JwtResponse(newAccessToken, newRefreshToken);
            }
        }

        throw new JwtAuthenticationException("JWT token is invalid", HttpStatus.FORBIDDEN);
    }

    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }
}
