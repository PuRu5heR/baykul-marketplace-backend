package by.baykulbackend.database.dao.order;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Order status enum")
public enum OrderStatus {
    CREATED,
    PAID,
    PROCESSING,
    COMPLETED,
    CANCELLED
}