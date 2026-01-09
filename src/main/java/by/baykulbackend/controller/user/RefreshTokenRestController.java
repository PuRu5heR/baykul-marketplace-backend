package by.baykulbackend.controller.user;

import by.baykulbackend.database.dao.user.RefreshToken;
import by.baykulbackend.database.model.Views;
import by.baykulbackend.database.repository.user.IRefreshTokenRepository;
import by.baykulbackend.exceptions.NotFoundException;
import by.baykulbackend.services.user.RefreshTokenService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/refresh-token")
@RequiredArgsConstructor
@Tag(name = "Refresh Token Management", description = "API for managing user refresh tokens and sessions")
@SecurityRequirement(name = "bearerAuth")
public class RefreshTokenRestController {
    private final IRefreshTokenRepository iRefreshTokenRepository;
    private final RefreshTokenService refreshTokenService;

    @Operation(
            summary = "Get all refresh tokens",
            description = "Retrieves all refresh tokens from the system. Requires users:write permission.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of refresh tokens retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Views.RefreshTokenView.Get.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token missing or invalid"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient permissions"
            )
    })
    @GetMapping
    @PreAuthorize("hasAnyAuthority('users:write')")
    @JsonView(Views.RefreshTokenView.Get.class)
    public List<RefreshToken> getAll() {
        return iRefreshTokenRepository.findAll();
    }

    @Operation(
            summary = "Get refresh token by ID",
            description = "Retrieves a specific refresh token by its UUID. Requires users:write permission.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Refresh token retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Views.RefreshTokenView.Get.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token missing or invalid"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient permissions"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Refresh token not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('users:write')")
    @JsonView(Views.RefreshTokenView.Get.class)
    public RefreshToken getOne(
            @Parameter(
                    description = "UUID of the refresh token to retrieve",
                    required = true
            )
            @PathVariable UUID id) {
        return iRefreshTokenRepository.findById(id).orElseThrow(() -> new NotFoundException("Refresh token not found"));
    }

    @Operation(
            summary = "Get refresh tokens by user ID",
            description = "Retrieves all refresh tokens for a specific user. Requires users:read permission.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User's refresh tokens retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Views.RefreshTokenView.Get.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token missing or invalid"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient permissions"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @GetMapping("/user/{id}")
    @PreAuthorize("hasAnyAuthority('users:read')")
    @JsonView(Views.RefreshTokenView.Get.class)
    public List<RefreshToken> getUserRefTokensByUserId(
            @Parameter(
                    description = "UUID of the user",
                    required = true
            )
            @PathVariable UUID id) {
        return refreshTokenService.findUserRefTokensByUserId(id);
    }

    @Operation(
            summary = "Get current user's refresh tokens",
            description = "Retrieves all refresh tokens for the currently authenticated user. " +
                    "Requires users:read permission.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Current user's refresh tokens retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Views.RefreshTokenView.Get.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token missing or invalid"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient permissions"
            )
    })
    @GetMapping("/user")
    @PreAuthorize("hasAnyAuthority('users:read')")
    @JsonView(Views.RefreshTokenView.Get.class)
    public List<RefreshToken> getUserRefreshTokens() {
        return refreshTokenService.findUserRefreshTokens();
    }

    @Operation(
            summary = "Delete refresh token",
            description = "Deletes a refresh token by ID. User can only delete their own tokens unless they are ADMIN. " +
                    "Requires users:read permission.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Refresh token deleted successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - user doesn't own the token",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token missing or invalid"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient permissions"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Refresh token not found"
            )
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('users:read')")
    public ResponseEntity<?> delete(
            @Parameter(
                    description = "UUID of the refresh token to delete",
                    required = true
            )
            @PathVariable UUID id) {
        return refreshTokenService.deleteById(id);
    }
}
