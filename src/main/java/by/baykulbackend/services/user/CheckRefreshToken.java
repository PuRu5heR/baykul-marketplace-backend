package by.baykulbackend.services.user;

import by.baykulbackend.database.dao.user.RefreshToken;
import by.baykulbackend.database.repository.user.IRefreshTokenRepository;
import by.baykulbackend.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CheckRefreshToken {
    private final IRefreshTokenRepository iRefreshTokenRepository;
    private final JwtProvider jwtProvider;

    @Scheduled(cron = "0 0 3 * * *")
    public void check() {
        List<RefreshToken> refreshTokens = iRefreshTokenRepository.findAll();
        boolean check = false;

        log.info("START OF VERIFICATION -> ");

        for (RefreshToken refreshToken : refreshTokens) {
            if (refreshToken.getName() == null) {
                iRefreshTokenRepository.delete(refreshToken);
                check = true;
                log.info("DELETE -> Refresh token with id = {}", refreshToken.getId());
            } else {
                if (!jwtProvider.validateRefreshToken(refreshToken.getName())) {
                    iRefreshTokenRepository.delete(refreshToken);
                    check = true;
                    log.info("DELETE -> Refresh token with id = {}", refreshToken.getId());
                }
            }
        }

        if (!check) {
            log.info("CHECK -> Expired refresh tokens were not found");
        }

        log.info("<- END OF VERIFICATION");
    }
}
