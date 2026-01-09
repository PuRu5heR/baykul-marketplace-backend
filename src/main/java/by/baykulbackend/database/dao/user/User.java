package by.baykulbackend.database.dao.user;

import by.baykulbackend.database.model.Role;
import by.baykulbackend.database.model.Views;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
@Entity
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Table(name = "users")
@Schema(description = "User entity representing system users with authentication and authorization data")
public class User {
    @Schema(
            description = "Unique identifier",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JsonView({Views.UserView.Get.class, Views.UserView.Post.class, Views.UserView.Put.class, Views.UserView.Profile.class})
    private UUID id;

    @Schema(
            description = "Timestamp when the user was created",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @NotNull
    @Column(name = "created_ts", updatable = false, nullable = false)
    @JsonView(Views.UserView.Get.class)
    private LocalDateTime createdTs;

    @Schema(
            description = "Timestamp when the user was last updated",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @NotNull
    @Column(name = "updated_ts", updatable = false, nullable = false)
    @JsonView(Views.UserView.Get.class)
    private LocalDateTime updatedTs;

    @Schema(
            description = "Unique username for authentication",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 1,
            maxLength = 50
    )
    @NotNull
    @Column(name = "login", nullable = false, unique = true, length = 50)
    @JsonView({Views.UserView.Get.class, Views.UserView.Post.class, Views.UserView.Put.class, Views.UserView.Profile.class})
    private String login;

    @Schema(
            description = "Hashed password (BCrypt)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            accessMode = Schema.AccessMode.WRITE_ONLY
    )
    @NotNull
    @Column(name = "password", nullable = false)
    @JsonView({Views.UserView.Post.class, Views.UserView.Put.class})
    private String password;

    @Schema(
            description = "User's email address",
            maxLength = 100,
            format = "email",
            nullable = true
    )
    @Column(name = "email", unique = true, length = 100)
    @JsonView({Views.UserView.Get.class, Views.UserView.Post.class, Views.UserView.Put.class, Views.UserView.Profile.class})
    private String email;

    @Schema(
            description = "User's phone number",
            maxLength = 20,
            format = "phone number",
            nullable = true
    )
    @Column(name = "phone_number", length = 20)
    @JsonView({Views.UserView.Get.class, Views.UserView.Post.class, Views.UserView.Put.class, Views.UserView.Profile.class})
    private String phoneNumber;

    @Schema(
            description = "User's role in the system",
            allowableValues = {"USER", "ADMIN"},
            requiredMode = Schema.RequiredMode.REQUIRED,
            defaultValue = "USER"
    )
    @NotNull
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    @JsonView({Views.UserView.Get.class, Views.UserView.Post.class, Views.UserView.Put.class})
    private Role role = Role.USER;

    @Schema(
            description = "Indicates if the user account is blocked",
            requiredMode = Schema.RequiredMode.REQUIRED,
            defaultValue = "false"
    )
    @NotNull
    @Column(name = "blocked", nullable = false)
    @JsonView({Views.UserView.Get.class, Views.UserView.Put.class})
    private Boolean blocked = false;

    @Schema(
            description = "List of refresh tokens associated with the user",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonView(Views.UserWithRefreshTokenView.class)
    private List<RefreshToken> refreshTokens;

    @Schema(
            description = "User's profile containing personal information",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @NotNull
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonView(Views.UserView.Profile.class)
    private Profile profile;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}