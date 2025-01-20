package com.example.outsourcingproject.domain.order.service;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.outsourcingproject.domain.menu.entity.Menu;
import com.example.outsourcingproject.domain.menu.repository.MenuRepository;
import com.example.outsourcingproject.domain.order.dto.ChangeOrderStatusRequestDto;
import com.example.outsourcingproject.domain.order.dto.ChangeOrderStatusResponseDto;
import com.example.outsourcingproject.domain.order.dto.CreateOrderRequestDto;
import com.example.outsourcingproject.domain.order.dto.OrderListDto;
import com.example.outsourcingproject.domain.order.dto.OrderListResponseDto;
import com.example.outsourcingproject.domain.order.entity.Order;
import com.example.outsourcingproject.domain.order.enums.OrderStatus;
import com.example.outsourcingproject.domain.order.repository.OrderRepository;
import com.example.outsourcingproject.domain.store.entity.Store;
import com.example.outsourcingproject.domain.store.repository.StoreRepository;
import com.example.outsourcingproject.domain.user.entity.User;
import com.example.outsourcingproject.domain.user.enums.UserRoleEnum;
import com.example.outsourcingproject.domain.user.repository.UserRepository;
import com.example.outsourcingproject.exception.ErrorCode;
import com.example.outsourcingproject.exception.GlobalExceptionHandler;
import com.example.outsourcingproject.exception.common.BusinessException;
import com.example.outsourcingproject.exception.common.MenuNotFoundException;
import com.example.outsourcingproject.exception.common.OrderNotFoundException;
import com.example.outsourcingproject.exception.common.StoreNotFoundException;
import com.example.outsourcingproject.exception.common.UserNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;
	private final StoreRepository storeRepository;
	private final MenuRepository menuRepository;
	private final UserRepository userRepository;
	private final GlobalExceptionHandler globalExceptionHandler;

	// 주문 생성
	@Transactional
	public Order createOrder(Long userId, CreateOrderRequestDto dto) {

		Menu menu = findMenuByIdOrElseThrow(dto.menuId());
		Store store = menu.getStore();

		User user = findUserByIdOrElseThrow(userId);

		checkMenuIsDeleted(menu);
		checkMinOrderAmount(store, menu.getPrice());
		checkOrderTimeWithinOperatingHours(store);

		Order order = new Order(user, store, menu, OrderStatus.PENDING, dto.cart());

		return orderRepository.save(order);
	}

	// 주문 상태 변경
	@Transactional
	public Order updateOrderStatus(Long userId, Long orderId, ChangeOrderStatusRequestDto dto) {
		User user = findUserByIdOrElseThrow(userId);
		Order order = findOrderByIdOrElseThrow(orderId);

		OrderStatus orderStatus = OrderStatus.from(dto.orderStatus());
		order.setOrderStatus(orderStatus);

		return order;
	}

	// 주문 내역 조회
	@Transactional(readOnly = true)
	public OrderListResponseDto findAllOrders(Long userId) {

		List<OrderListDto> orderListDtoList = orderRepository.findByUser_UserId(userId)
			.stream()
			.map(OrderListDto::mapToDto)
			.toList();

		return OrderListResponseDto.convertFrom(orderListDtoList);
	}

	/* 예외처리 */
	private Store findStoreByIdOrElseThrow(Long storeId) {
		return storeRepository.findById(storeId)
			.orElseThrow(StoreNotFoundException::new);
	}

	private Menu findMenuByIdOrElseThrow(Long menuId) {
		return menuRepository.findById(menuId)
			.orElseThrow(MenuNotFoundException::new);
	}

	private User findUserByIdOrElseThrow(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);
	}

	private Order findOrderByIdOrElseThrow(Long orderId){
		return orderRepository.findById(orderId)
			.orElseThrow(OrderNotFoundException::new);
	}

	/* 메서드 */
	private static void checkOrderTimeWithinOperatingHours(Store store) {
		LocalTime orderTime = LocalTime.now();
		LocalTime openTime = dateTimeFormat(store.getOpenTime());
		LocalTime closeTime = dateTimeFormat(store.getCloseTime());

		if (orderTime.isBefore(openTime) || orderTime.isAfter(closeTime)) {
			throw new BusinessException(ErrorCode.INVALID_ORDER_TIME);
		}
	}

	private static LocalTime dateTimeFormat(String stringTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		try {
			return LocalTime.parse(stringTime, formatter);
		} catch (DateTimeException e) {
			throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "잘못된 시간 형식입니다. HH:mm 형식으로 입력해주세요.");
		}
	}

	private static void checkMinOrderAmount(Store store, int totalAmount) {
		if (totalAmount < store.getMinOrderAmount()) {
			throw new BusinessException(ErrorCode.INVALID_TOTALAMOUNT);
		}
	}

	private void checkMenuIsDeleted(Menu menu) {
		if (menu.isDeleted()){
			throw new MenuNotFoundException("메뉴를 찾을 수 없습니다. menuId: " + menu.getMenuId());
		}
	}
}
