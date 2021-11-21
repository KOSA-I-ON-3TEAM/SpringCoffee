package com.kosa.springcoffee.order;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByOrderNo(Long orderNo);

    @Query("select o from sc_order o " + "where o.member.email = :email " )
    List<Order> findOrders(@Param("email") String email, Pageable pageable);

    @Query("select o from sc_order o")
    List<Order> findOrdersforAdmin(Pageable pageable);


    @Query("select count(o) from sc_order o")
    Long countAllOrder();

    @Query("select count(o) from sc_order o where o.member.email = :email")
    Long countOrder(@Param("email")String email);


}
