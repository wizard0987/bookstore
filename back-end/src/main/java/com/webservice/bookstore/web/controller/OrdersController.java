package com.webservice.bookstore.web.controller;

import com.webservice.bookstore.config.security.auth.CustomUserDetails;
import com.webservice.bookstore.service.OrdersService;
import com.webservice.bookstore.web.dto.ItemDto;
import com.webservice.bookstore.web.dto.MemberDto;
import com.webservice.bookstore.web.dto.OrderItemDto;
import com.webservice.bookstore.web.dto.OrdersDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class OrdersController {

    private final OrdersService orderService;

    /*
    주문 생성
    */
//    @PostMapping(value = "/order")
//    public ResponseEntity createOrder(@RequestBody List<OrderItemDto> orderItemDtoList,
    @PostMapping(value = "/order")
    public ResponseEntity createOrder(@RequestBody List<Map<String, String>> list,
                                      @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        MemberDto memberDto = null;
        try {
            memberDto = MemberDto.builder()
                                .id(customUserDetails.getMember().getId())
                                .build();
        } catch (NullPointerException e) {
            // CustomUserDetails 객체가 null인 경우는 jwt 토큰으로 인증을 거치지 않았다는 의미
            throw new AuthenticationException("인증 오류가 발생했습니다.", e.getCause()) {};
        }

        List<OrderItemDto> orderItemDtoList = new ArrayList<>();
        for(Map<String, String> map : list) {
            ItemDto itemDto = ItemDto.builder().id(Long.parseLong(map.get("item_id"))).build();
            orderItemDtoList.add(OrderItemDto.builder()
                                            .itemDto(itemDto)
                                            .orderCount(Integer.parseInt(map.get("orderCount")))
                                            .build()
                                );
        }

        OrdersDto ordersDto = orderService.addOrder(memberDto, orderItemDtoList);

        return new ResponseEntity(ordersDto, HttpStatus.OK);
    }

    /*
    관리자 페이지 각 회원 주문 리스트 (구매 내역) 조회
    */
    @GetMapping("/admin/members/{member_id}/orders")
    public ResponseEntity getOrderList(@PathVariable(value = "member_id") Long member_id) {

        MemberDto memberDto = MemberDto.builder().id(member_id).build();

        List<OrdersDto> orderDtoList = orderService.findOrders(memberDto);

        return new ResponseEntity(orderDtoList, HttpStatus.OK);
    }

    /*
    관리자 페이지 주문 취소
    */
    @PatchMapping("/admin/orders/cancel/{order_id}")
    public ResponseEntity cancelOrder(@RequestBody @PathVariable(value = "order_id") Long orders_id,
                                      @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        MemberDto memberDto = MemberDto.builder().id(customUserDetails.getMember().getId()).build();
        OrdersDto ordersDto = OrdersDto.builder().id(orders_id).build();

        orderService.cancelOrder(memberDto, ordersDto);

        return new ResponseEntity("success", HttpStatus.OK);
    }
}
