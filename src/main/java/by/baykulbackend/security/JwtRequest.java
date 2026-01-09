package by.baykulbackend.security;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtRequest {

    @NotBlank
    @Schema(description = "Username", requiredMode = Schema.RequiredMode.REQUIRED)
    private String login;

    @NotBlank
    @Schema(description = "Password", requiredMode = Schema.RequiredMode.REQUIRED, format = "password")
    private String password;
}
