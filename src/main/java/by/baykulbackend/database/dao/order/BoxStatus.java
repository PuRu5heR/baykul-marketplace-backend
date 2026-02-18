package by.baykulbackend.database.dao.order;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Schema(description = "Box status enum")
public enum BoxStatus {
    ORDERED,
    IN_WAREHOUSE,
    ON_WAY,
    ARRIVED,
    DELIVERED,
    RETURNED,
    CANCELLED;

    private static final Map<BoxStatus, List<BoxStatus>> NEXT_STATUSES = new EnumMap<>(BoxStatus.class);

    static {
        NEXT_STATUSES.put(ORDERED, List.of(IN_WAREHOUSE, CANCELLED));
        NEXT_STATUSES.put(IN_WAREHOUSE, List.of(ON_WAY, CANCELLED));
        NEXT_STATUSES.put(ON_WAY, List.of(ARRIVED, RETURNED));
        NEXT_STATUSES.put(ARRIVED, List.of(DELIVERED, RETURNED));
        NEXT_STATUSES.put(DELIVERED, List.of(RETURNED));
        NEXT_STATUSES.put(RETURNED, List.of());
        NEXT_STATUSES.put(CANCELLED, List.of());
    }

    /**
     * Returns list of possible next statuses for current status
     * @return list of available next statuses
     */
    public List<BoxStatus> getNextStatuses() {
        return NEXT_STATUSES.get(this);
    }

    /**
     * Checks if transition to target status is allowed
     * @param targetStatus target status to check
     * @return true if transition is allowed, false otherwise
     */
    public boolean canTransitionTo(BoxStatus targetStatus) {
        return getNextStatuses().contains(targetStatus);
    }
}
