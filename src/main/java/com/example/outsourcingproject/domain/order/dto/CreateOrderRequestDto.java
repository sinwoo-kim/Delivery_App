package com.example.outsourcingproject.domain.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateOrderRequestDto(

	@NotBlank(message = "menuId는 필수입니다.")
	Long menuId,

	@Min(value = 1, message = "음식은 최소 1개 이상 주문해야 합니다.")
	int cart
) {
}
