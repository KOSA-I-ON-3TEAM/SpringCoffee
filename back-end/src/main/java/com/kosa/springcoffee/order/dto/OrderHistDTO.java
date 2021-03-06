package com.kosa.springcoffee.order.dto;

import com.kosa.springcoffee.order.Order;
import com.kosa.springcoffee.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter @Setter
@AllArgsConstructor
public class OrderHistDTO {
    private Long orderNo;
    private OrderStatus orderStatus;
    private List<OrderItemDTO> orderItemDTOList = new ArrayList<>();

    public OrderHistDTO(Order order) {
        this.orderNo = order.getOrderNo();
        this.orderStatus = order.getStatus();
    }


    public void addOrderItemDto(OrderItemDTO dto) {
        orderItemDTOList.add(dto);
    }
}
