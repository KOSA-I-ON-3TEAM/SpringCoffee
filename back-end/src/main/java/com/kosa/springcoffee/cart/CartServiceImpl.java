package com.kosa.springcoffee.cart;

import com.kosa.springcoffee.cart.dto.CartDetailDTO;
import com.kosa.springcoffee.cart.dto.CartItemDTO;
import com.kosa.springcoffee.order.dto.OrderDTO;
import com.kosa.springcoffee.item.Item;
import com.kosa.springcoffee.item.ItemRepository;
import com.kosa.springcoffee.member.Member;
import com.kosa.springcoffee.member.MemberRepository;
import com.kosa.springcoffee.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final OrderService orderService;


    @Override
    public Long create(CartItemDTO cartItemDTO, String email) {
        Member member = memberRepository.getByEmail(email);
        Cart cart = cartRepository.findByMemberEmail(member.getEmail());
        if (cart == null){
            cart = Cart.createCart(member);
            cartRepository.save(cart);

        }

        Item item = itemRepository.findByItemNo(cartItemDTO.getItemNo());
        //Item item = itemRepository.findById(cartItemDTO.getItemNo()).orElseThrow(EntityNotFoundException::new);
        CartItem cartItem = cartItemRepository.findByCartNoAndItemNo(cart.getCartNo(), item.getItemNo());

        if (cartItem == null){
            cartItem = CartItem.createCartItem(cart, item, cartItemDTO.getCount());
            cartItemRepository.save(cartItem);
        }
        else{
            cartItem.addCount(cartItemDTO.getCount());
            cartItemRepository.save(cartItem);
        }
        return cartItem.getCartItemNo();
    }

    @Override
    public List<CartDetailDTO> getCartList(String email) {
        List<CartDetailDTO> cartDetailDTOList =new ArrayList<>();

        Member member = memberRepository.getByEmail(email);
        Cart cart = cartRepository.findByMemberEmail(member.getEmail());
        if(cart == null)
            return cartDetailDTOList;

        cartDetailDTOList = cartItemRepository.findCartDetialDtoList(cart.getCartNo());
        return cartDetailDTOList;
    }

    @Override
    public boolean validateCartItem(Long cartItemNo, String email) {
        Member curMember = memberRepository.getByEmail(email);
        CartItem cartItem = cartItemRepository.findByCartItemNo(cartItemNo);
        Member savedMember = cartItem.getCart().getMember();

        if (!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){
            return true;
        }
        return false;
    }

    @Override
    public void changeCartItemCount(Long cartItemNo, int count) {
        CartItem cartItem = cartItemRepository.findByCartItemNo(cartItemNo);
        cartItem.changeCount(count);
        cartItemRepository.save(cartItem);
    }

    @Override
    public void deleteCartItem(Long cartItemNo) {
        CartItem cartItem = cartItemRepository.findByCartItemNo(cartItemNo);
        cartItemRepository.delete(cartItem);
    }

    @Override
    public Long orderCartItem(List<Long> cartOrderDtoList, String email,String address) {
        List<OrderDTO> orderDTOList = new ArrayList<>();

        for (Long cartItemNo : cartOrderDtoList){
            CartItem cartItem = cartItemRepository.findByCartItemNo(cartItemNo);
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setItemNo(cartItem.getItem().getItemNo());
            orderDTO.setCount(cartItem.getCount());
            orderDTOList.add(orderDTO);
        }

        Long orderNo = orderService.cartOrder(orderDTOList, email,address);

        for (Long cartItemNo : cartOrderDtoList){
            CartItem cartItem = cartItemRepository.findByCartItemNo(cartItemNo);
            cartItemRepository.delete(cartItem);
        }
        return orderNo;
    }
}