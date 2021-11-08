package com.kosa.springcoffee.service;

import com.kosa.springcoffee.dto.OrderDTO;
import com.kosa.springcoffee.dto.OrderDTO2;
import com.kosa.springcoffee.dto.OrderHistDTO;
import com.kosa.springcoffee.dto.OrderItemDTO;
import com.kosa.springcoffee.entity.Item;
import com.kosa.springcoffee.entity.Member;
import com.kosa.springcoffee.entity.Order;
import com.kosa.springcoffee.entity.OrderItem;
import com.kosa.springcoffee.repository.ItemRepository;
import com.kosa.springcoffee.repository.MemberRepository;
import com.kosa.springcoffee.repository.OrderItemRepository;
import com.kosa.springcoffee.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

//    @Override
//    public Long create(OrderDTO orderDTO) {
//        Order entity = dtoToEntity(orderDTO);
//        List<OrderItem> getListOrderItem = entity.getOrderItems();
//        Iterator<OrderItem> iter = getListOrderItem.stream().iterator();
//        while(iter.hasNext()){
//            orderItemRepository.deleteById(iter.next().getOrderItemNo());
//            iter.next().getItem().removeStock(iter.next().getCount());
    //         replyRepo.deleteByReplyNo(iter.next);
    //         iter.next()
//        }
//        return entity.getOrderNo();
//    }

    @Override
    public Long create(OrderDTO2 orderDTO2, String email) {
        Item item = itemRepository.findById(orderDTO2.getItemNo()).orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email);

        List<OrderItem> orderItemList = new ArrayList<>();


        OrderItem orderItem = OrderItem.createOrderItem(item, orderDTO2.getCount());
        orderItemList.add(orderItem);
        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        return order.getOrderNo();
    }

//    @Override
//    public void orderCancel(Long orderNo) {
//        Order order = orderRepository.findByOrderNo(orderNo);
//        List<OrderItem> getListOrderItem = order.getOrderItems();
//        Iterator<OrderItem> iter = getListOrderItem.stream().iterator();
//        while(iter.hasNext()) {
//            iter.next().getItem().addStock(iter.next().getCount());
//        }
//        order.cancel();
//    }

    @Override
    public Page<OrderHistDTO> getOrderList(String email, Pageable pageable) {
        List<Order> orders = orderRepository.findOrders(email, pageable);
        Long totalCount = orderRepository.countOrder(email);

        List<OrderHistDTO> orderHistDtos = new ArrayList<>();

        for (Order order : orders) {
            OrderHistDTO orderHistDto = new OrderHistDTO(order);
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                OrderItemDTO orderItemDto = new OrderItemDTO(orderItem);
                orderHistDto.addOrderItemDto(orderItemDto);
            }
            orderHistDtos.add(orderHistDto);
        }
        return new PageImpl<>(orderHistDtos, pageable, totalCount);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderNo, String email) {
        Order order = orderRepository.findById(orderNo).orElseThrow(EntityNotFoundException::new);

        if (StringUtils.equals(order.getMember().getEmail(), email)) {
            return true;
        }
        return false;
    }

    @Override
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        order.cancel();
    }

}
