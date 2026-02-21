package by.baykulbackend.services.bill;

import by.baykulbackend.database.dao.bill.Bill;
import by.baykulbackend.database.dao.bill.BillStatus;
import by.baykulbackend.database.dao.order.BoxStatus;
import by.baykulbackend.database.dao.order.OrderProduct;
import by.baykulbackend.database.repository.bill.IBillRepository;
import by.baykulbackend.database.repository.order.IOrderProductRepository;
import by.baykulbackend.services.user.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillService {
    private final IOrderProductRepository iOrderProductRepository;
    private final IBillRepository iBillRepository;
    private final AuthService authService;

    @Transactional
    public ResponseEntity<?> createBill(Bill bill) {
        Map<String, Object> response = new HashMap<>();

        Bill newBill = new Bill();
        newBill.setStatus(BillStatus.DRAFT);

        List<OrderProduct> orderProducts = null;

        if (bill.getOrderProducts() != null) {
            orderProducts = new ArrayList<>();
            List<UUID> unavailableOrderProductIds = new ArrayList<>();

            //noinspection SimplifyStreamApiCallChains
            Set<UUID> orderProductIdSet = bill.getOrderProducts().stream()
                    .filter(op -> op.getId() != null)
                    .map(OrderProduct::getId)
                    .collect(Collectors.toSet());

            for (UUID orderProductId : orderProductIdSet) {
                iOrderProductRepository.findByBillIsNullAndIdAndStatus(orderProductId, BoxStatus.ORDERED).ifPresentOrElse(
                        op -> op.setBill(newBill),
                        () -> unavailableOrderProductIds.add(orderProductId)
                );
            }

            if (unavailableOrderProductIds.isEmpty()) {
                response.put("unavailable_order_products", unavailableOrderProductIds);
            }
        }

        newBill.setOrderProducts(orderProducts);
        iBillRepository.save(newBill);

        if (orderProducts != null) {
            iOrderProductRepository.saveAll(orderProducts);
        }

        response.put("create_bill", "true");
        response.put("id", newBill.getId().toString());

        log.info("Bill {} created -> {}", newBill.getId(), authService.getAuthInfo().getPrincipal());

        return ResponseEntity.ok(response);
    }


}
