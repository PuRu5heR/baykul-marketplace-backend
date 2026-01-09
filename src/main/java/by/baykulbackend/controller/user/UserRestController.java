package by.baykulbackend.controller.user;

import by.baykulbackend.database.dao.user.User;
import by.baykulbackend.database.model.Views;
import by.baykulbackend.database.repository.user.IUserRepository;
import by.baykulbackend.exceptions.NotFoundException;
import by.baykulbackend.services.user.UserService;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "API for user registration, management, and search operations")
public class UserRestController {
    private final IUserRepository iUserRepository;
    private final UserService userService;

    @Operation(
            summary = "Get all users",
            description = "Retrieves all users from the system with their refresh tokens. Requires users:write permission.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of users retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Views.UserWithRefreshTokenView.class))
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
    @PreAuthorize("hasAnyAuthority('users:write')")
    @JsonView(Views.UserWithRefreshTokenView.class)
    @GetMapping
    public List<User> getAll() {
        return iUserRepository.findAll();
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves a specific user by UUID with their refresh tokens. Requires users:read permission.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Views.UserWithRefreshTokenView.class)
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
                    description = "User not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            )
    })
    @PreAuthorize("hasAnyAuthority('users:read')")
    @JsonView(Views.UserWithRefreshTokenView.class)
    @GetMapping("/{id}")
    public User getOne(
            @Parameter(
                    description = "UUID of the user to retrieve",
                    required = true
            )
            @PathVariable UUID id) {
        return iUserRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Operation(
            summary = "Search users",
            description = "Searches users by login, email, or phone number containing the specified text. " +
                    "Requires users:write permission.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Search results retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Views.UserWithRefreshTokenView.class))
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
    @PreAuthorize("hasAnyAuthority('users:write')")
    @JsonView(Views.UserWithRefreshTokenView.class)
    @GetMapping("/search/{text}")
    public List<User> search(
            @Parameter(
                    description = "Text to search for in login, email, or phone number",
                    required = true
            )
            @PathVariable String text) {
        return userService.searchUser(text);
    }

    @Operation(
            summary = "Search users by login",
            description = "Searches users by login containing the specified text (case-insensitive). " +
                    "Requires users:write permission.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Search results retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Views.UserWithRefreshTokenView.class))
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
    @PreAuthorize("hasAnyAuthority('users:write')")
    @JsonView(Views.UserWithRefreshTokenView.class)
    @GetMapping("/search-login/{login}")
    public List<User> getByLogin(
            @Parameter(
                    description = "Login text to search for (case-insensitive)",
                    required = true
            )
            @PathVariable String login) {
        return iUserRepository.findByLoginContainingIgnoreCase(login);
    }

    @Operation(
            summary = "Search users by email",
            description = "Searches users by email containing the specified text (case-insensitive). " +
                    "Requires users:write permission.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Search results retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Views.UserWithRefreshTokenView.class))
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
    @PreAuthorize("hasAnyAuthority('users:write')")
    @JsonView(Views.UserWithRefreshTokenView.class)
    @GetMapping("/search-email/{email}")
    public List<User> getByEmail(
            @Parameter(
                    description = "Email text to search for (case-insensitive)",
                    required = true
            )
            @PathVariable String email) {
        return iUserRepository.findByEmailContainingIgnoreCase(email);
    }

    @Operation(
            summary = "Search users by phone number",
            description = "Searches users by phone number containing the specified text. " +
                    "Requires users:write permission.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Search results retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Views.UserWithRefreshTokenView.class))
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
    @PreAuthorize("hasAnyAuthority('users:write')")
    @JsonView(Views.UserWithRefreshTokenView.class)
    @GetMapping("/search-phone-number/{phoneNumber}")
    public List<User> getByPhoneNumber(
            @Parameter(
                    description = "Phone number text to search for",
                    required = true
            )
            @PathVariable String phoneNumber) {
        return iUserRepository.findByPhoneNumberContaining(phoneNumber);
    }

    @Operation(
            summary = "Create new user",
            description = "Creates a new user in the system. Requires users:write permission.",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User data to create",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Views.UserView.Post.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User created successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - invalid user data"
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
                    responseCode = "409",
                    description = "Conflict - user with same login/email/phone already exists"
            )
    })
    @PreAuthorize("hasAnyAuthority('users:write')")
    @JsonView(Views.UserView.Get.class)
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @JsonView(Views.UserView.Post.class) User user) {
        return userService.createUser(user);
    }

    @Operation(
            summary = "Register new user",
            description = "Registers a new user in the system. No authentication required. Returns validation errors if registration fails.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User registration data",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Views.UserView.Post.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User registered successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - validation errors",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - user with same login/email/phone already exists",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            )
    })
    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody @JsonView(Views.UserView.Post.class) User user) {
        return userService.registerUser(user);
    }

    @Operation(
            summary = "Update user",
            description = "Updates an existing user's information. Only non-null fields are updated. Requires users:read permission.",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User data to create",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Views.UserView.Put.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token missing or invalid",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient permissions",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - user with same login/email/phone already exists",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            )
    })
    @Transactional
    @PreAuthorize("hasAnyAuthority('users:read')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @Parameter(
                    description = "UUID of the user to update",
                    required = true
            )
            @PathVariable UUID id,
            @RequestBody @JsonView(Views.UserView.Put.class) User user) {
        return userService.updateUser(id, user);
    }

    @Operation(
            summary = "Delete user",
            description = "Deletes a user by ID. Requires users:write permission.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User deleted successfully",
                    content = @Content(
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - user not found",
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
                    description = "User not found"
            )
    })
    @PreAuthorize("hasAnyAuthority('users:write')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @Parameter(
                    description = "UUID of the user to delete",
                    required = true
            )
            @PathVariable UUID id) {
        return userService.deleteUserById(id);
    }
}
