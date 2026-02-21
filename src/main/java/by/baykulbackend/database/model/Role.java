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
                      
                      - **USER** - Standard user with permissions: users:read, balances:read, products:read, carts:read, orders:read
                      - **MANAGER** - Manager with permissions: users:read, balances:read/write, products:read/write, carts:read, orders:read/write, bills:read/write
                      - **ADMIN** - Full system access
                      """,
        enumAsRef = true
)
public enum Role {
    USER(Set.of(Permission.USERS_READ, Permission.BALANCE_READ, Permission.PRODUCT_READ, Permission.CART_READ,
            Permission.ORDER_READ)),
    MANAGER(Set.of(Permission.USERS_READ, Permission.BALANCE_READ, Permission.BALANCE_WRITE,
            Permission.PRODUCT_READ, Permission.PRODUCT_WRITE, Permission.CART_READ, Permission.ORDER_READ,
            Permission.ORDER_WRITE, Permission.BILL_READ, Permission.BILL_WRITE)),
    ADMIN(Set.of(Permission.USERS_READ, Permission.USERS_WRITE, Permission.BALANCE_READ, Permission.BALANCE_WRITE,
            Permission.PRODUCT_READ, Permission.PRODUCT_WRITE, Permission.CART_READ, Permission.CART_WRITE,
            Permission.ORDER_READ, Permission.ORDER_WRITE, Permission.BILL_READ, Permission.BILL_WRITE)),;

    private final Set<Permission> permissions;

    private static final String DEFAULT_PREFIX = "ROLE_";

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<SimpleGrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());

        authorities.add(new SimpleGrantedAuthority(DEFAULT_PREFIX + this.name()));

        return authorities;
    }
}
