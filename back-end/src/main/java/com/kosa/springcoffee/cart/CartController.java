package com.kosa.springcoffee.cart;

import com.kosa.springcoffee.cart.dto.*;
import com.kosa.springcoffee.item.Item;
import com.kosa.springcoffee.item.ItemRepository;
import com.kosa.springcoffee.member.Member;
import com.kosa.springcoffee.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v5")
@Log4j2
public class CartController {
    private final CartService cartService;
    private final MemberRepository memberRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;


    @PostMapping("/cart")
    public ResponseEntity cart(@RequestBody CartItemTestDTO cartItemDTO){
        Long cartItemNo;
        CartItemDTO dto = new CartItemDTO();
        dto.setCount(cartItemDTO.getCount());
        dto.setItemNo(cartItemDTO.getItemNo());
        Member member = memberRepository.getByEmail(cartItemDTO.getEmail());
        cartItemNo = cartService.create(dto,member.getEmail());
        return new ResponseEntity<Long>(cartItemNo, HttpStatus.OK);
    }

    @GetMapping(value = "/cart/{email}")
    public ResponseEntity cartList(@PathVariable String email) {
        Member member = memberRepository.getByEmail(email);
        List<CartDetailDTO> cartDetailDTOList = cartService.getCartList(member.getEmail());
        List<CartListResponseDTO> cartListResponseDTOList = new ArrayList<>();

        for(CartDetailDTO detail : cartDetailDTOList) {
            Item item = itemRepository.findByName(detail.getItemName());
            CartListResponseDTO dto = CartListResponseDTO.builder()
                    .cartItemNo(detail.getCartItemNo())
                    .count(detail.getCount())
                    .fileId(item.getItemImg().get(0).getItemImgNo())
                    .price(detail.getPrice())
                    .itemName(detail.getItemName())
                    .build();

            System.out.println(detail.getItemName());
            cartListResponseDTOList.add(dto);
        }
        return new ResponseEntity<>(cartListResponseDTOList, HttpStatus.OK);
    }


    @PatchMapping(value = "/cartItem")
    public @ResponseBody ResponseEntity changeCartItemCount(@RequestBody CartPatchDTO cartPatchDTO){
        int count = cartPatchDTO.getCount();
        Long cartItemNo = cartPatchDTO.getCartItemNo();

        Member member = memberRepository.getByEmail(cartPatchDTO.getEmail());
        if (count < 0){
            return new ResponseEntity<String>("?????? 1??? ?????? ??????????????????.", HttpStatus.BAD_REQUEST);
        }
        else if(cartService.validateCartItem(cartItemNo, member.getEmail()))
            return new ResponseEntity<String>("????????? ????????? ????????????.", HttpStatus.FORBIDDEN);

        cartService.changeCartItemCount(cartItemNo,count);
        CartItem cartItem = cartItemRepository.findByCartItemNo(cartItemNo);
        System.out.println(cartItem.getCount());
        System.out.println();


        return new ResponseEntity<Long>(cartItemNo, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{cartItemNo}/{email}")
    public ResponseEntity deleteCartItem(@PathVariable("email")String email, @PathVariable("cartItemNo") Long cartItemNo){
        Member member = memberRepository.getByEmail(email);
        if(cartService.validateCartItem(cartItemNo, member.getEmail()))
            return new ResponseEntity<String>("????????? ????????? ????????????.", HttpStatus.FORBIDDEN);
        cartService.deleteCartItem(cartItemNo);
        return new ResponseEntity<Long>(cartItemNo, HttpStatus.OK);
    }


    @PostMapping(value = "/cartOrder")
    @ResponseBody
    public ResponseEntity orderCartItem(@RequestBody CartOrderRequestDTO requestDTO){
        Member member= memberRepository.getByEmail(requestDTO.getEmail());
        String address = requestDTO.getAddress();
        System.out.println("????????? : " + requestDTO.getEmail() + "  ?????? : " + requestDTO.getAddress());
        List<Long> cartOrderDTOList = requestDTO.getCartItemNo();
        if (cartOrderDTOList == null || cartOrderDTOList.size() == 0){
            return new ResponseEntity<String>("????????? ????????????.", HttpStatus.BAD_REQUEST);
        }
        for (Long num : cartOrderDTOList){
            if(cartService.validateCartItem(num , member.getEmail()))
                return new ResponseEntity<String>("????????? ????????? ????????????.", HttpStatus.FORBIDDEN);
        }
        Long orderNo = cartService.orderCartItem(cartOrderDTOList, member.getEmail(), address);
        return new ResponseEntity<Long>(orderNo, HttpStatus.OK);
    }
}