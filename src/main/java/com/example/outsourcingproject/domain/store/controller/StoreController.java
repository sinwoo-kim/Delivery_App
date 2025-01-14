package com.example.outsourcingproject.domain.store.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.outsourcingproject.common.ApiResponse;
import com.example.outsourcingproject.domain.store.dto.StoreCloseResponseDto;
import com.example.outsourcingproject.domain.store.dto.StoreCreateRequestDto;
import com.example.outsourcingproject.domain.store.dto.StoreCreateResponseDto;
import com.example.outsourcingproject.domain.store.dto.StoreUpdateRequestDto;
import com.example.outsourcingproject.domain.store.dto.StoreUpdateResponseDto;
import com.example.outsourcingproject.domain.store.service.StoreService;
import com.example.outsourcingproject.domain.user.entity.User;
import com.example.outsourcingproject.domain.user.enums.UserRoleEnum;
import com.example.outsourcingproject.domain.user.service.UserService;
import com.example.outsourcingproject.exception.auth.UnauthorizedException;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

	private final StoreService storeService;
	private final UserService userService;
	@PostMapping
	@Operation(summary = "가게 생성", description = "사장님(OWNER) 권한을 가진 사용자만 가게를 생성할 수 있습니다.")
	public ResponseEntity<ApiResponse<StoreCreateResponseDto>> createStore(
		@AuthenticationPrincipal Long userId,
		@Valid @RequestBody StoreCreateRequestDto requestDto
	) {
		// // 사용자 권한 검증 (추가적인 검증이 필요한 경우)
		// User user = userService.getUserById(userId);
		// if (!user.getRole().equals(UserRoleEnum.OWNER)) {
		// 	throw new UnauthorizedException("가게 생성은 사장님만 가능합니다.");
		// }

		StoreCreateResponseDto responseDto = storeService.createStore(
			requestDto,
			userId
		);

		return ResponseEntity
			.created(URI.create("/api/stores/" + responseDto.storeId()))
			.body(ApiResponse.success("가게가 성공적으로 등록되었습니다.", responseDto));
	}

	@PutMapping("/{storeId}")
	@Operation(summary = "가게 수정", description = "사장님(OWNER) 권한을 가진 사용자만 가게를 수정 할 수 있습니다.")
	public ResponseEntity<ApiResponse<StoreUpdateResponseDto>> updateStore(
		@PathVariable Long storeId,
		@AuthenticationPrincipal Long userId,  // JWT 구현 후 수정 필요
		@Valid @RequestBody StoreUpdateRequestDto requestDto
	) {
		StoreUpdateResponseDto responseDto = storeService.updateStore(storeId, userId, requestDto);

		return ResponseEntity.ok(ApiResponse.success("가게 정보가 성공적으로 수정되었습니다.", responseDto));
	}

	@GetMapping
	@Operation(summary = "가게 조회", description = "모든 유저는 가게를 조회 할 수 있습니다.")
	public ResponseEntity<ApiResponse<?>> getStores(
		@RequestParam(required = false) String storeName
	) {
		return ResponseEntity.ok(storeService.getStores(storeName));
	}

	@PutMapping("/{storeId}/close")
	@Operation(summary = "가게 폐업", description = "사장님(OWNER) 권한을 가진 사용자만 가게를 폐업할 수 있습니다.")
	public ResponseEntity<ApiResponse<StoreCloseResponseDto>> closeStore(
		@PathVariable Long storeId,
		@AuthenticationPrincipal Long userId
	) {
		StoreCloseResponseDto responseDto = storeService.closeStore(storeId, userId);
		return ResponseEntity.ok(ApiResponse.success("가게가 성공적으로 폐업 처리되었습니다.", responseDto));
	}
}
