package by.baykulbackend.database.repository.order;

import by.baykulbackend.database.dao.order.Order;
import by.baykulbackend.database.dao.order.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IOrderProductRepository extends JpaRepository<OrderProduct, UUID> {
    void deleteAllByOrder(Order cart);
    boolean existsByNumber(Long number);
}
