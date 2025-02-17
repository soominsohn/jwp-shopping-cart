package woowacourse.shoppingcart.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woowacourse.shoppingcart.dao.*;
import woowacourse.shoppingcart.domain.OrderDetail;
import woowacourse.shoppingcart.dto.OrderRequest;
import woowacourse.shoppingcart.domain.Orders;
import woowacourse.shoppingcart.domain.Product;
import woowacourse.shoppingcart.dto.OrdersResponse;
import woowacourse.shoppingcart.exception.InvalidOrderException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class OrderService {

    private final OrderDao orderDao;
    private final OrdersDetailDao ordersDetailDao;
    private final CartItemDao cartItemDao;
    private final CustomerDao customerDao;
    private final ProductDao productDao;

    public OrderService(final OrderDao orderDao, final OrdersDetailDao ordersDetailDao,
                        final CartItemDao cartItemDao, final CustomerDao customerDao, final ProductDao productDao) {
        this.orderDao = orderDao;
        this.ordersDetailDao = ordersDetailDao;
        this.cartItemDao = cartItemDao;
        this.customerDao = customerDao;
        this.productDao = productDao;
    }

    public Long addOrder(final List<OrderRequest> orderDetailRequests, final String customerName) {
        final Long customerId = customerDao.findIdByUserName(customerName);
        final Long ordersId = orderDao.addOrders(customerId);

        for (final OrderRequest orderDetail : orderDetailRequests) {
            final Long cartId = orderDetail.getCartId();
            final Long productId = cartItemDao.findProductIdById(cartId);
            final int quantity = orderDetail.getQuantity();

            ordersDetailDao.addOrdersDetail(ordersId, productId, quantity);
            cartItemDao.deleteCartItem(cartId);
        }

        return ordersId;
    }

    public OrdersResponse findOrderById(final String customerName, final Long orderId) {
        validateOrderIdByCustomerName(customerName, orderId);
        return findOrderResponseDtoByOrderId(orderId);
    }

    private void validateOrderIdByCustomerName(final String customerName, final Long orderId) {
        final Long customerId = customerDao.findIdByUserName(customerName);

        if (!orderDao.isValidOrderId(customerId, orderId)) {
            throw new InvalidOrderException("유저에게는 해당 order_id가 없습니다.");
        }
    }

    public List<OrdersResponse> findOrdersByCustomerName(final String customerName) {
        final Long customerId = customerDao.findIdByUserName(customerName);
        final List<Long> orderIds = orderDao.findOrderIdsByCustomerId(customerId);

        return orderIds.stream()
                .map(this::findOrderResponseDtoByOrderId)
                .collect(Collectors.toList());
    }

    private OrdersResponse findOrderResponseDtoByOrderId(final Long orderId) {
        final List<OrderDetail> ordersDetails = new ArrayList<>();
        for (final OrderDetail productQuantity : ordersDetailDao.findOrdersDetailsByOrderId(orderId)) {
            final Product product = productDao.findProductById(productQuantity.getProductId());
            final int quantity = productQuantity.getQuantity();
            ordersDetails.add(new OrderDetail(product, quantity));
        }
        Orders orders = new Orders(orderId, ordersDetails);

        return OrdersResponse.of(orders);
    }
}
