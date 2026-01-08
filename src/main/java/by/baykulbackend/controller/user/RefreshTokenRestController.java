package by.baykulbackend.controller.user;

import by.baykulbackend.database.dao.user.RefreshToken;
import by.baykulbackend.database.model.Views;
import by.baykulbackend.database.repository.user.IRefreshTokenRepository;
import by.baykulbackend.exceptions.NotFoundException;
import by.baykulbackend.services.user.RefreshTokenService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/refresh-token")
@RequiredArgsConstructor
public class RefreshTokenRestController {
    private final IRefreshTokenRepository iRefreshTokenRepository;
    private final RefreshTokenService refreshTokenService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('users:write')")
    @JsonView(Views.RefreshTokenView.Get.class)
    public List<RefreshToken> getAll() {
        return iRefreshTokenRepository.findAll();
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('users:write')")
    @JsonView(Views.RefreshTokenView.Get.class)
    public RefreshToken getOne(@PathVariable UUID id) {
        return iRefreshTokenRepository.findById(id).orElseThrow(() -> new NotFoundException("Refresh token not found"));
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasAnyAuthority('users:read')")
    @JsonView(Views.RefreshTokenView.Get.class)
    public List<RefreshToken> getUserRefTokensByUserId(@PathVariable UUID id) {
        return refreshTokenService.findUserRefTokensByUserId(id);
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyAuthority('users:read')")
    @JsonView(Views.RefreshTokenView.Get.class)
    public List<RefreshToken> getUserRefreshTokens() {
        return refreshTokenService.findUserRefreshTokens();
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyAuthority('users:read')")
    @JsonView(Views.RefreshTokenView.Put.class)
    public RefreshToken update(@JsonView(Views.RefreshTokenView.Put.class) RefreshToken refreshToken) {
        return iRefreshTokenRepository.save(refreshToken);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('users:read')")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        return refreshTokenService.deleteById(id);
    }
}
