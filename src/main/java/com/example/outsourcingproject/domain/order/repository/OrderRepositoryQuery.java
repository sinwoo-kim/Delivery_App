package com.example.outsourcingproject.domain.order.repository;

import java.util.List;

import com.example.outsourcingproject.domain.order.entity.Order;

public interface OrderRepositoryQuery {
	List<Order> findByUser_UserId(Long userId);
}
