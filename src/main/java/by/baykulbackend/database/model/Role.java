package by.baykulbackend.database.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Schema(description = """
                      User role in the system. Defines permissions and access levels.
                      
                      **Available roles:**
                      
                      - **USER** - Standard user with permissions: users:read, balances:read, products:read, carts:read
                      - **MANAGER** - Manager with permissions: users:read, balances:read/write, products:read/write
                      - **ADMIN** - Full system access
                      """,
        enumAsRef = true
)
public enum Role {
    USER(Set.of(Permission.USERS_READ, Permission.BALANCE_READ, Permission.PRODUCT_READ, Permission.CART_READ)),
    MANAGER(Set.of(Permission.USERS_READ, Permission.BALANCE_READ, Permission.BALANCE_WRITE,
            Permission.PRODUCT_READ, Permission.PRODUCT_WRITE)),
    ADMIN(Set.of(Permission.USERS_READ, Permission.USERS_WRITE, Permission.BALANCE_READ, Permission.BALANCE_WRITE,
            Permission.PRODUCT_READ, Permission.PRODUCT_WRITE, Permission.CART_READ, Permission.CART_WRITE));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<SimpleGrantedAuthority> getAuthorities() {
        return getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
    }
}
