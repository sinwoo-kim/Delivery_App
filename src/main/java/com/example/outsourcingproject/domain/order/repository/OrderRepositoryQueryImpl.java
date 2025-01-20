package com.example.outsourcingproject.domain.order.repository;

import static com.example.outsourcingproject.domain.order.entity.QOrder.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.outsourcingproject.domain.order.entity.Order;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class OrderRepositoryQueryImpl implements OrderRepositoryQuery {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<Order> findByUser_UserId(Long userId) {
		return jpaQueryFactory
			.selectFrom(order)
			.join(order.store).fetchJoin()
			.join(order.menu).fetchJoin()
			.where(order.user.userId.eq(userId))
			.fetch();
	}
}
