package com.example.outsourcingproject.domain.order.logging;

import java.time.LocalDateTime;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.example.outsourcingproject.domain.order.entity.Order;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class OrderLogging {

	@AfterReturning(
		pointcut = "execution(* com.example.outsourcingproject.domain.order.service.OrderService.createOrder(..))",
		returning = "result"
	)
	public void createOrderLog(Object result) {
		if (result instanceof Order order) {
			log.info("신규 주문. RequestTime: {}, StoreId: {}, OrderId: {}",
				LocalDateTime.now(), order.getOrderId());
		}
	}

	@AfterReturning(
		pointcut = "execution(* com.example.outsourcingproject.domain.order.service.OrderService.updateOrderStatus(..))",
		returning = "result"
	)
	public void updateOrderLog(Object result) {
		if (result instanceof Order order) {
			log.info("주문 상태를 {}(으)로 변경. RequestTime: {}, StoreId: {}, OrderId: {}",
				order.getOrderStatus(), LocalDateTime.now(), order.getOrderId());
		}
	}
}
