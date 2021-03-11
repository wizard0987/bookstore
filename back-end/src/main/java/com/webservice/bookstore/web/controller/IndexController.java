package com.webservice.bookstore.web.controller;

import com.webservice.bookstore.domain.entity.item.ItemLinkResource;
import com.webservice.bookstore.service.ItemService;
import com.webservice.bookstore.service.ItemServices;
import com.webservice.bookstore.web.dto.ItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/")
@Log4j2
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000"})
public class IndexController {

    private final ItemServices itemService;

    @GetMapping("/index/image/")
    public ResponseEntity<List<ItemDto>> getPromotionalImage() {

        log.info("Index 홍보 이미지");

        List<ItemDto> itemDtoList = itemService.getRandomList(3);

        return new ResponseEntity<>(itemDtoList, HttpStatus.OK);
    }

    @GetMapping("/index/thismonth/")
    public ResponseEntity<List<ItemDto>> getThisMonthList(){
        log.info("이달의 도서 보내기");

        List<ItemDto> list=itemService.getRandomList(12);

        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    @GetMapping("/index/wepickitem/")
    public ResponseEntity<List<ItemDto>> getWePickItem(){
        log.info("우리의 PICK 보내기");
        return new ResponseEntity<>(itemService.getRandomList(12) ,HttpStatus.OK);
    }

    /*
    hover 시 각 장르별 item 정보 3개 랜덤 조회 요청
    */
    @GetMapping(value = "/genre/")
    public ResponseEntity<List<ItemDto>> getRandomListByGenre(@RequestBody ItemDto itemDto) {

        List<ItemDto> itemDtoList = itemService.getRandomListByGenre(itemDto);

        return new ResponseEntity<>(itemDtoList, HttpStatus.OK);
    }

    /*
    hover 시 나타난 팝업 창 내 genre 중 하나 버튼 click 시, 각 장르별 item 리스트 조회 요청
    */
    @GetMapping(value = "/genre/{category_id}/", produces = MediaTypes.HAL_JSON_VALUE+";charset=utf-8")
    public ResponseEntity<CollectionModel> getListByGenre(@PathVariable("category_id") Long category_id) {

        List<ItemDto> itemList = itemService.getListByGenre(category_id);

        List<ItemLinkResource> emList = itemList.stream()
                .map(itemDto -> new ItemLinkResource(itemDto,
                        linkTo(methodOn(IndexController.class).getListByGenre(itemDto.getId())).withSelfRel()))
                .collect(Collectors.toList());

        CollectionModel<ItemLinkResource> collectionModel = CollectionModel.of(emList);

        return new ResponseEntity(collectionModel, HttpStatus.OK);
    }
}