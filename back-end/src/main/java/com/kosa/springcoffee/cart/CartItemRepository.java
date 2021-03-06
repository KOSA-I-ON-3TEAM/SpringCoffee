package com.kosa.springcoffee.cart;

import com.kosa.springcoffee.cart.dto.CartDetailDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("select ci from sc_cart_item ci where ci.cart.cartNo = :cartNo and ci.item.itemNo = :itemNo")
    CartItem findByCartNoAndItemNo(@Param("cartNo") Long cartNo,@Param("itemNo") Long itemNo);

    CartItem findByCartItemNo(Long cartItemNo);

    @Query("select new com.kosa.springcoffee.cart.dto.CartDetailDTO(ci.cartItemNo, i.name, i.price, ci.count) " +
            "from sc_cart_item  ci " +
            "join ci.item i " +
            "where ci.cart.cartNo = :cartNo ")
    List<CartDetailDTO> findCartDetialDtoList(Long cartNo);
}