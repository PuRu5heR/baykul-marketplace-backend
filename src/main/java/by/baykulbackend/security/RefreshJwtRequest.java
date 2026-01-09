package by.baykulbackend.security;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Refresh token request")
public class RefreshJwtRequest {

    @NotBlank
    @Schema(description = "Refresh token", requiredMode = Schema.RequiredMode.REQUIRED)
    public String refreshToken;
}
