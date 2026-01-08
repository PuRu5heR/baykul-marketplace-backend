package by.baykulbackend.security;

import by.baykulbackend.database.model.JwtAuthentication;
import by.baykulbackend.database.model.Role;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtUtils {
    public static JwtAuthentication generate(Claims claims) {
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setRole(getRole(claims));
        jwtInfoToken.setId(claims.get("id", Long.class));
        jwtInfoToken.setLogin(claims.getSubject());

        return jwtInfoToken;
    }

    private static Role getRole(Claims claims) {
        final String role = claims.get("role", String.class);
        return Enum.valueOf(Role.class, role);
    }
}
