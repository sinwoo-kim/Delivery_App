// package com.example.outsourcingproject.domain.review.service;
//
//
// import com.example.outsourcingproject.domain.order.entity.Order;
// import com.example.outsourcingproject.domain.order.enums.OrderStatus;
// import com.example.outsourcingproject.domain.order.repository.OrderRepository;
// import com.example.outsourcingproject.domain.review.dto.ReviewRequest;
// import com.example.outsourcingproject.domain.review.dto.ReviewResponse;
// import com.example.outsourcingproject.domain.review.entity.Review;
// import com.example.outsourcingproject.domain.review.repository.ReviewRepository;
// import com.example.outsourcingproject.domain.store.entity.Store;
// import com.example.outsourcingproject.domain.user.entity.User;
// import com.example.outsourcingproject.exception.common.BusinessException;
// import com.example.outsourcingproject.exception.common.OrderNoDeivered;
// import com.example.outsourcingproject.exception.common.OrderNotFoundException;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.mock;
// import static org.mockito.Mockito.when;
//
//
// @SpringBootTest
// class ReviewServiceTest {
//
//     @MockBean
//     private OrderRepository orderRepository;
//
//     @MockBean
//     private ReviewRepository reviewRepository;
//
//     @Autowired
//     private ReviewService reviewService;
//
//     @Test
//     @WithMockUser(username = "mockUser", roles = "CUSTOMER")
//     void createReview() {
//         // given
//         User mockUser = mock(User.class);
//         when(mockUser.getUserId()).thenReturn(1L);
//
//         Store mockStore = mock(Store.class);
//         when(mockStore.getStoreId()).thenReturn(1L);
//
//         Order mockOrder = mock(Order.class);
//         when(mockOrder.getOrderId()).thenReturn(1L);
//         when(mockOrder.getUser()).thenReturn(mockUser);
//         when(mockOrder.getStore()).thenReturn(mockStore);
//         when(mockOrder.getOrderStatus()).thenReturn(OrderStatus.DELIVERED);  // 추가된 부분
//
//         ReviewRequest reviewRequest = new ReviewRequest(1L, 5, "Test Content");
//
//         when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
//         when(reviewRepository.save(Mockito.any(Review.class)))
//                 .thenReturn(new Review(1L, mockOrder, mockUser, mockStore, 5, "Test Content", LocalDateTime.now()));
//
//         // when
//         ReviewResponse actualResponse = reviewService.createReview(reviewRequest, 1L);
//
//         // then
//         assertNotNull(actualResponse);
//         assertEquals(1L, actualResponse.orderId());
//         assertEquals(1L, actualResponse.userId());
//         assertEquals(1L, actualResponse.storeId());
//         assertEquals(5, actualResponse.rating());
//         assertEquals("Test Content", actualResponse.content());
//     }
//
//     @Test
//     void createReview_shouldThrowOrderNotFoundException_whenOrderNotFound() {
//         // given
//         ReviewRequest reviewRequest = new ReviewRequest(1L, 5, "Test Content");
//
//         // when
//         when(orderRepository.findById(1L)).thenReturn(Optional.empty());
//
//         // then
//         assertThrows(OrderNotFoundException.class, () -> {
//             reviewService.createReview(reviewRequest, 1L);
//         });
//     }
//
//     @Test
//     void createReview_shouldThrowOrderNoDeivered_whenOrderStatusIsNotDelivered() {
//         // given
//         User mockUser = mock(User.class);
//         when(mockUser.getUserId()).thenReturn(1L);
//
//         Store mockStore = mock(Store.class);
//         when(mockStore.getStoreId()).thenReturn(1L);
//
//         Order mockOrder = mock(Order.class);
//         when(mockOrder.getOrderId()).thenReturn(1L);
//         when(mockOrder.getUser()).thenReturn(mockUser);
//         when(mockOrder.getStore()).thenReturn(mockStore);
//         when(mockOrder.getOrderStatus()).thenReturn(OrderStatus.PENDING);  // DELIVERED가 아닌 상태로 수정
//
//
//         ReviewRequest reviewRequest = new ReviewRequest(1L, 5, "Test Content");
//
//         when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
//
//         // when & then
//         assertThrows(OrderNoDeivered.class, () -> {
//             reviewService.createReview(reviewRequest, 1L);
//         });
//     }
//
//     @Test
//     void createReview_shouldThrowBusinessException_whenUserIsNotTheAuthor() {
//         // given
//         User mockUser = mock(User.class);
//         when(mockUser.getUserId()).thenReturn(1L);
//
//         User differentUser = mock(User.class);  // 다른 유저
//         when(differentUser.getUserId()).thenReturn(2L);
//
//         Store mockStore = mock(Store.class);
//         when(mockStore.getStoreId()).thenReturn(1L);
//
//         Order mockOrder = mock(Order.class);
//         when(mockOrder.getOrderId()).thenReturn(1L);
//         when(mockOrder.getUser()).thenReturn(differentUser);  // 다른 유저의 주문
//         when(mockOrder.getStore()).thenReturn(mockStore);
//         when(mockOrder.getOrderStatus()).thenReturn(OrderStatus.DELIVERED);
//
//         ReviewRequest reviewRequest = new ReviewRequest(1L, 5, "Test Content");
//
//         when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
//
//         // when & then
//         assertThrows(BusinessException.class, () -> {
//             reviewService.createReview(reviewRequest, 1L);
//         });
//     }
//
//     @Test
//     @WithMockUser(username = "mockUser", roles = "CUSTOMER")
//     void createReview_shouldThrowBusinessException_whenRatingOutOfRange() {
//         // given
//         User mockUser = mock(User.class);
//         when(mockUser.getUserId()).thenReturn(1L);
//
//         Store mockStore = mock(Store.class);
//         when(mockStore.getStoreId()).thenReturn(1L);
//
//         Order mockOrder = mock(Order.class);
//         when(mockOrder.getOrderId()).thenReturn(1L);
//         when(mockOrder.getUser()).thenReturn(mockUser);
//         when(mockOrder.getStore()).thenReturn(mockStore);
//         when(mockOrder.getOrderStatus()).thenReturn(OrderStatus.DELIVERED);
//
//         // 잘못된 별점 (0 또는 6)으로 ReviewRequest 생성
//         ReviewRequest reviewRequest = new ReviewRequest(1L, 0, "Test Content");
//
//         when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
//
//         // when & then
//         assertThrows(BusinessException.class, () -> {
//             reviewService.createReview(reviewRequest, 1L);
//         });
//     }
//
//     @Test
//     void getReviewById() {
//         //given
//         Long storeId = 1L;
//         // Mock Store
//         Store mockStore = mock(Store.class);
//         when(mockStore.getStoreId()).thenReturn(storeId);
//
//         // Mock User
//         User mockUser = mock(User.class);
//         when(mockUser.getUserId()).thenReturn(1L);
//
//         // Mock Order
//         Order mockOrder = mock(Order.class);
//         when(mockOrder.getOrderId()).thenReturn(1L);
//
//         // Mock Review
//         Review mockReview = mock(Review.class);
//         when(mockReview.getReviewId()).thenReturn(1L);
//         when(mockReview.getOrder()).thenReturn(mockOrder);
//         when(mockReview.getUser()).thenReturn(mockUser);
//         when(mockReview.getStore()).thenReturn(mockStore);
//         when(mockReview.getRating()).thenReturn(5);
//         when(mockReview.getContent()).thenReturn("Excellent!");
//         when(mockReview.getCreatedAt()).thenReturn(LocalDateTime.now());
//
//         // Mock Review List
//         List<Review> mockReviews = List.of(mockReview);
//
//         // Mock Repository Behavior
//         when(reviewRepository.findByStore_StoreIdOrderByCreatedAtDesc(storeId)).thenReturn(mockReviews);
//
//         //실제 데이터
//         List<Review> reviews = reviewRepository.findByStore_StoreIdOrderByCreatedAtDesc(storeId);
//         when(reviewRepository.findByStore_StoreIdOrderByCreatedAtDesc(storeId)).thenReturn(mockReviews);
//
//         List<ReviewResponse> actualResponses = reviewService.getReviewsByStoreId(storeId);
//         assertEquals(1, actualResponses.size());
//         ReviewResponse actualResponse = actualResponses.get(0);
//
//         assertEquals(mockReview.getReviewId(), actualResponse.reviewId());
//         assertEquals(mockOrder.getOrderId(), actualResponse.orderId());
//         assertEquals(mockUser.getUserId(), actualResponse.userId());
//         assertEquals(mockStore.getStoreId(), actualResponse.storeId());
//         assertEquals(mockReview.getRating(), actualResponse.rating());
//         assertEquals(mockReview.getContent(), actualResponse.content());
//         assertEquals(mockReview.getCreatedAt(), actualResponse.createdAt());
//
//     }
//
//     @Test
//     void getReviewsByStoreIdAndRating(){
//         Long storeId = 1L;
//         int minRating = 3;
//         int maxRating = 5;
//
//         Store mockStore = mock(Store.class);
//         when(mockStore.getStoreId()).thenReturn(storeId);
//
//         User mockUser = mock(User.class);
//         when(mockUser.getUserId()).thenReturn(1L);
//
//         Order mockOrder = mock(Order.class);
//         when(mockOrder.getOrderId()).thenReturn(1L);
//
//         // 조건에 맞는 리뷰
//         Review reviewInRange = mock(Review.class);
//         when(reviewInRange.getReviewId()).thenReturn(1L);
//         when(reviewInRange.getOrder()).thenReturn(mockOrder);
//         when(reviewInRange.getUser()).thenReturn(mockUser);
//         when(reviewInRange.getStore()).thenReturn(mockStore);
//         when(reviewInRange.getRating()).thenReturn(4); // 조건에 부합하는 점수
//         when(reviewInRange.getContent()).thenReturn("Good!");
//         when(reviewInRange.getCreatedAt()).thenReturn(LocalDateTime.now());
//
//         //조건에 맞지 않는 리뷰
//         Review reviewOutRange = mock(Review.class);
//         when(reviewOutRange.getReviewId()).thenReturn(1L);
//         when(reviewOutRange.getOrder()).thenReturn(mockOrder);
//         when(reviewOutRange.getUser()).thenReturn(mockUser);
//         when(reviewOutRange.getStore()).thenReturn(mockStore);
//         when(reviewOutRange.getRating()).thenReturn(1); // 조건에 부합하지 않는 점수
//         when(reviewOutRange.getContent()).thenReturn("Good!");
//         when(reviewOutRange.getCreatedAt()).thenReturn(LocalDateTime.now());
//
//         List<Review> mockReviewsInRange = List.of(reviewInRange);
//         when(reviewRepository.findByStore_StoreIdAndRatingBetweenOrderByCreatedAtDesc(storeId, minRating, maxRating))
//                 .thenReturn(mockReviewsInRange);
//
//         // when
//         List<ReviewResponse> actualResponses = reviewService.getReviewsByStoreIdAndRating(storeId, minRating, maxRating);
//
//         //then
//         assertEquals(1, actualResponses.size());
//         ReviewResponse actualResponse = actualResponses.get(0);
//
//         assertEquals(reviewInRange.getReviewId(), actualResponse.reviewId());
//         assertEquals(mockOrder.getOrderId(), actualResponse.orderId());
//         assertEquals(mockUser.getUserId(), actualResponse.userId());
//         assertEquals(mockStore.getStoreId(), actualResponse.storeId());
//         assertEquals(reviewInRange.getRating(), actualResponse.rating());
//         assertEquals(reviewInRange.getContent(), actualResponse.content());
//         assertEquals(reviewInRange.getCreatedAt(), actualResponse.createdAt());
//
//
//
//     }
//
//
//
// }