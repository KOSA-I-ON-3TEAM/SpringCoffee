package com.kosa.springcoffee.order;

import com.kosa.springcoffee.order.dto.OrderDTO;
import com.kosa.springcoffee.order.dto.OrderHistDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    Long create(OrderDTO orderDTO, String email);
    Page<OrderHistDTO> getOrderListForAdmin(Pageable pageable);
    Page<OrderHistDTO> getOrderList(String email, Pageable pageable);
    boolean validateOrder(Long orderNo, String email);

    void cancelOrder(Long orderNo);
    void shippingOrder(Long orderNo);
    void doneOrder(Long orderNo);
    void prepareOrder(Long orderNo);
    Long cartOrder(List<OrderDTO> orderDTOList, String email, String address);


}
