package by.baykulbackend.controller.user;

import by.baykulbackend.database.dao.user.RefreshToken;
import by.baykulbackend.database.repository.user.IRefreshTokenRepository;
import by.baykulbackend.services.user.AuthService;
import by.baykulbackend.security.JwtRequest;
import by.baykulbackend.security.JwtResponse;
import by.baykulbackend.security.RefreshJwtRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and token management")
public class AuthenticationRestController {
    private final AuthService authService;
    private final IRefreshTokenRepository iRefreshTokenRepository;

    @Operation(
            summary = "User login",
            description = "Authenticate user with login and password. Returns JWT access and refresh tokens.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User credentials",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JwtRequest.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JwtResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - invalid input or missing required fields",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - invalid credentials, user blocked, or user not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Schema(description = "Client/browser identifier")
            @RequestHeader(value = "User-Agent")
            String userAgent,
            HttpServletRequest request,
            @RequestBody JwtRequest authRequest) {
        final JwtResponse token = authService.login(userAgent, request, authRequest);

        return ResponseEntity.ok(token);
    }

    @Operation(
            summary = "Get new access token",
            description = "Generate a new access token using a valid refresh token. " +
                    "Used when access token expires. Returns only access token.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Refresh Token",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RefreshJwtRequest.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "New access token generated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JwtResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - invalid refresh token format or missing token"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - invalid, expired, or non-existent refresh token",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            )
    })
    @PostMapping("/token")
    public ResponseEntity<JwtResponse> getNewAccessToken(@RequestBody RefreshJwtRequest request,
                                                         HttpServletRequest httpServletRequest) {
        final JwtResponse token = authService.getAccessToken(request.getRefreshToken(), httpServletRequest);

        return ResponseEntity.ok(token);
    }

    @Operation(
            summary = "Refresh both tokens",
            description = "Generate new pair of access and refresh tokens. Invalidates the old refresh token.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Refresh Token",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RefreshJwtRequest.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "New token pair generated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JwtResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - invalid refresh token format"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - invalid, expired, or non-existent refresh token",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            )
    })
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> getNewRefreshToken(@RequestBody RefreshJwtRequest request,
                                                          HttpServletRequest httpServletRequest) {
        final JwtResponse token = authService.refresh(request.getRefreshToken(), httpServletRequest);

        return ResponseEntity.ok(token);
    }

    @Operation(
            summary = "User logout",
            description = "Invalidate refresh token and clear security context. Requires valid refresh token.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Refresh Token",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RefreshJwtRequest.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Logout successful - no content returned"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - invalid refresh token format"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found - refresh token doesn't exist in database",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            )
    })
    @PostMapping("/logout")
    public void logout(@RequestBody RefreshJwtRequest refreshToken, HttpServletRequest request, HttpServletResponse response) {
        RefreshToken refreshTokenFromDb = iRefreshTokenRepository.findRefreshTokenByName(refreshToken.getRefreshToken());
        iRefreshTokenRepository.deleteById(refreshTokenFromDb.getId());
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request, response, null);
    }
}
