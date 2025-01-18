package com.example.outsourcingproject.domain.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.outsourcingproject.domain.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
	@Query("SELECT o FROM Order o JOIN FETCH o.store JOIN FETCH o.menu WHERE o.user.userId = :userId" )
	List<Order> findByUser_UserId(@Param("userId") Long userId);
}
