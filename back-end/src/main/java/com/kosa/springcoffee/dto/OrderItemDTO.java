package com.kosa.springcoffee.dto;

import com.kosa.springcoffee.entity.Item;
import com.kosa.springcoffee.entity.Order;
import com.kosa.springcoffee.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderItemDTO {
    private Long orderItemNo;
    private Long itemNo;
    private Long orderNo;
    private int allPrice;
    private int count;

    public static OrderItem dtoToEntityOrderItem(OrderItemDTO dto){
        OrderItem entity = OrderItem.builder()
                .orderItemNo(dto.getOrderItemNo())
                .item(Item.builder().itemNo(dto.getItemNo()).build())
                .order(Order.builder().orderNo(dto.getOrderNo()).build())
                .allPrice(dto.getAllPrice())
                .count(dto.getCount())
                .build();
        return entity;
    }

    public static OrderItemDTO entityToDtoOrderItem(OrderItem entity){
        OrderItemDTO dto = OrderItemDTO.builder()
                .orderItemNo(entity.getOrderItemNo())
                .itemNo(entity.getItem().getItemNo())
                .orderNo(entity.getOrder().getOrderNo())
                .allPrice(entity.getAllPrice())
                .count(entity.getCount())
                .build();
        return dto;
    }

}
