package com.webservice.bookstore.domain.entity.item;

import com.webservice.bookstore.web.controller.ItemController;
import com.webservice.bookstore.web.dto.GetItemDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ItemResource extends EntityModel<GetItemDto> {

    public ItemResource(GetItemDto item, Link... links) {
        super(item, links);
        add(linkTo(ItemController.class).slash(item.getId()).withSelfRel());
    }
}
