package com.example.outsourcingproject.domain.review.service;

import com.example.outsourcingproject.domain.order.entity.Order;
import com.example.outsourcingproject.domain.order.enums.OrderStatus;
import com.example.outsourcingproject.domain.order.repository.OrderRepository;
import com.example.outsourcingproject.domain.review.dto.ReviewRequest;
import com.example.outsourcingproject.domain.review.dto.ReviewResponse;
import com.example.outsourcingproject.domain.review.entity.Review;
import com.example.outsourcingproject.domain.review.repository.ReviewRepository;
import com.example.outsourcingproject.domain.store.service.StoreService;
import com.example.outsourcingproject.domain.user.entity.User;
import com.example.outsourcingproject.domain.user.service.UserService;
import com.example.outsourcingproject.domain.order.service.OrderService;
import com.example.outsourcingproject.exception.ErrorCode;
import com.example.outsourcingproject.exception.GlobalExceptionHandler;
import com.example.outsourcingproject.exception.common.BusinessException;
import com.example.outsourcingproject.exception.common.OrderNoDeivered;
import com.example.outsourcingproject.exception.common.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;  // OrderRepository
    private final UserService userService; // 유저 서비스
    private final StoreService storeService; // 가게 서비스

    // 주문을 ID로 찾는 메서드
    private Order findOrderByIdOrElseThrow(Long orderId) {
        return orderRepository.findById(orderId)
               .orElseThrow(() -> new OrderNotFoundException(ErrorCode.ORDER_NOT_FOUND));
    }

    // 주문 검증 메서드: 주문 상태가 'DELIVERED'여야 하고, 작성자는 본인만
    private void validateOrder(Long orderId, Long userId) {
        Order order = findOrderByIdOrElseThrow(orderId);

        // 주문 상태가 'DELIVERED'여야만 리뷰를 작성할 수 있음
        if (!order.getOrderStatus().equals(OrderStatus.DELIVERED)) {
           throw new OrderNoDeivered(ErrorCode.ORDER_NOT_DELIVERED);
        }

        // 주문한 유저만 리뷰를 작성할 수 있음
        if (!order.getUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS, "본인의 주문만 리뷰를 작성할 수 있습니다.");
        }
    }

    // 리뷰 생성 메서드
    public ReviewResponse createReview(ReviewRequest reviewRequest, Long userId) {
        // 별점 유효성 검증 (1~5 사이)
        if (reviewRequest.rating() < 1 || reviewRequest.rating() > 5) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS,"별점은 1부터 5까지의 값만 입력할 수 있습니다.");
        }

        // 주문 유효성 검증
        validateOrder(reviewRequest.orderId(), userId);

        // 주문에 연결된 가게 ID 가져오기
        Long storeId = findOrderByIdOrElseThrow(reviewRequest.orderId()).getStore().getStoreId();
        Order order = findOrderByIdOrElseThrow(reviewRequest.orderId());
        User user = userService.getUserById(userId);

        // Review 엔티티 생성
        Review review = Review.created(order, user, order.getStore(), reviewRequest.rating(), reviewRequest.content());

        // 리뷰 저장
        Review savedReview = reviewRepository.save(review);

        // 응답 객체 생성
        return new ReviewResponse(
                savedReview.getReviewId(),
                savedReview.getOrder().getOrderId(),
                savedReview.getUser().getUserId(),
                savedReview.getStore().getStoreId(),
                savedReview.getRating(),
                savedReview.getContent(),
                savedReview.getCreatedAt()
        );
    }

    // 가게별 리뷰 조회
    public List<ReviewResponse> getReviewsByStoreId(Long storeId) {
        // storeId를 기준으로 리뷰 조회
        List<Review> reviews = reviewRepository.findByStore_StoreIdOrderByCreatedAtDesc(storeId);

        // 리뷰 리스트를 ReviewResponse로 변환
        return reviews.stream()
                .map(review -> new ReviewResponse(
                        review.getReviewId(),
                        review.getOrder().getOrderId(),
                        review.getUser().getUserId(),
                        review.getStore().getStoreId(),
                        review.getRating(),
                        review.getContent(),
                        review.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    // 가게별 별점 범위에 맞는 리뷰 조회
    public List<ReviewResponse> getReviewsByStoreIdAndRating(Long storeId, int minRating, int maxRating) {
        // storeId와 별점 범위를 기준으로 리뷰 조회
        List<Review> reviews = reviewRepository.findByStore_StoreIdAndRatingBetweenOrderByCreatedAtDesc(storeId, minRating, maxRating);

        // 리뷰 리스트를 ReviewResponse로 변환
        return reviews.stream()
                .map(review -> new ReviewResponse(
                        review.getReviewId(),
                        review.getOrder().getOrderId(),
                        review.getUser().getUserId(),
                        review.getStore().getStoreId(),
                        review.getRating(),
                        review.getContent(),
                        review.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}
