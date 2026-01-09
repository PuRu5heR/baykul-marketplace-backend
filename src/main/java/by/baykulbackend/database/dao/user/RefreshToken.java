package by.baykulbackend.database.dao.user;

import by.baykulbackend.database.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "refresh_token")
@Schema(description = "Refresh token entity for JWT authentication session management")
public class RefreshToken {
    @Schema(
            description = "Unique identifier",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JsonView({Views.RefreshTokenView.Get.class, Views.RefreshTokenView.Put.class, Views.UserWithRefreshTokenView.class})
    private UUID id;

    @Schema(
            description = "JWT refresh token string",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @Column(name = "name")
    @JsonView({Views.RefreshTokenView.Get.class, Views.UserWithRefreshTokenView.class})
    private String name;

    @Schema(
            description = "User agent string of the client device/browser",
            nullable = true
    )
    @Column(name = "user_agent")
    @JsonView({Views.RefreshTokenView.Get.class, Views.RefreshTokenView.Put.class, Views.UserWithRefreshTokenView.class})
    public String userAgent;

    @Schema(
            description = "IP address of the client",
            nullable = true
    )
    @Column(name = "ip_address")
    @JsonView({Views.RefreshTokenView.Get.class, Views.RefreshTokenView.Put.class, Views.UserWithRefreshTokenView.class})
    public String ipAddress;

    @NotNull
    @Schema(
            description = "User associated with this refresh token",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonView({Views.RefreshTokenView.Get.class, Views.UserWithRefreshTokenView.class})
    private User user;
}