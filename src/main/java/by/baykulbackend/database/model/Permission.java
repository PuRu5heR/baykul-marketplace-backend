package by.baykulbackend.database.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Permission enum defining system permissions")
public enum Permission {
    USERS_READ("users:read"),
    USERS_WRITE("users:write"),
    PRODUCT_READ("products:read"),
    PRODUCT_WRITE("products:write"),
    BALANCE_READ("balances:read"),
    BALANCE_WRITE("balances:write"),
    CART_READ("carts:read"),
    CART_WRITE("carts:write"),
    ORDER_READ("orders:read"),
    ORDER_WRITE("orders:write"),
    BILL_READ("bills:read"),
    BILL_WRITE("bills:write");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

}
