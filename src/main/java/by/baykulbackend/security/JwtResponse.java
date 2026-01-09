package by.baykulbackend.security;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "JWT tokens response")
public class JwtResponse {

    @Schema(description = "Token type", defaultValue = "Bearer")
    private final String type = "Bearer";

    @Schema(description = "Access token for API authorization (valid for 5 minutes)")
    private String accessToken;

    @Schema(description = "Refresh token for obtaining new access tokens (valid for 30 days)")
    private String refreshToken;
}
