package com.webservice.bookstore.domain.entity.cart;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @EntityGraph(value = "Cart.item")
    List<Cart> findByMemberId(Long member_id);

    Optional<Cart> findByMemberIdAndItemId(Long member_id, Long item_id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from Cart c where c.id in :cartIdList")
    void deleteAllByIdInQuery(@Param("cartIdList") List<Long> cartIdList);
}