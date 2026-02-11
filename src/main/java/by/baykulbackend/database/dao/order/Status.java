package by.baykulbackend.database.dao.order;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Status enum")
public enum Status {
    NEW, PROCESSING, COMPLETED, CANCELLED
}