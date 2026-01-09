package by.baykulbackend.database.dao.user;

import by.baykulbackend.database.model.Views;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Table(name = "profile")
@Schema(description = "User profile entity containing personal information")
public class Profile {
    @Schema(
            description = "Unique identifier",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JsonView(Views.UserView.Profile.class)
    private UUID id;

    @Schema(
            description = "Timestamp when the profile was created",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @NotNull
    @Column(name = "created_ts", updatable = false)
    @JsonView(Views.UserView.Profile.class)
    private LocalDateTime createdTs;

    @Schema(
            description = "Timestamp when the profile was last updated",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @NotNull
    @Column(name = "updated_ts")
    @JsonView(Views.UserView.Profile.class)
    private LocalDateTime updatedTs;

    @Schema(
            description = "User associated with this profile",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    @JsonView(Views.UserView.Profile.class)
    private User user;

    @Schema(
            description = "User's surname",
            maxLength = 50,
            nullable = true
    )
    @Column(name = "surname", length = 50)
    @JsonView({Views.UserView.Get.class, Views.UserView.Profile.class})
    private String surname;

    @Schema(
            description = "User's name",
            maxLength = 50,
            nullable = true
    )
    @Column(name = "name", length = 50)
    @JsonView({Views.UserView.Get.class, Views.UserView.Profile.class})
    private String name;

    @Schema(
            description = "User's patronymic",
            maxLength = 50,
            nullable = true
    )
    @Column(name = "patronymic", length = 50)
    @JsonView({Views.UserView.Get.class, Views.UserView.Profile.class})
    private String patronymic;
}