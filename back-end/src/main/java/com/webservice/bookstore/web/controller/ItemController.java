package com.webservice.bookstore.web.controller;

import com.webservice.bookstore.domain.entity.item.Item;
import com.webservice.bookstore.service.ItemService;
import com.webservice.bookstore.web.dto.ItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/index")
@Log4j2
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://locahost:3000"})
public class ItemController{

    private final ItemService itemService;

    @GetMapping("/thismonth")
    public ResponseEntity<List<ItemDto>> getThisMonthList(){
        log.info("이달의 도서 보내기");

       List<ItemDto> list=itemService.getRandomList(12);

        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    @GetMapping("/wepickitem")
    public ResponseEntity<List<ItemDto>> getWePickItem(){
        log.info("우리의 PICK 보내기");
        return new ResponseEntity<>(itemService.getRandomList(12) ,HttpStatus.OK);
    }
}
