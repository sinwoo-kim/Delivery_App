package com.example.outsourcingproject.domain.review.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.example.outsourcingproject.common.TimeStamped;
import com.example.outsourcingproject.domain.order.entity.Order;
import com.example.outsourcingproject.domain.store.entity.Store;
import com.example.outsourcingproject.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "reviews")
@NoArgsConstructor
@AllArgsConstructor
public class Review extends TimeStamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long reviewId; 

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order; // 주문 정보

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user; // 사용자 정보

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", nullable = false)
	private Store store; // 가게 정보

	@Column(nullable = false)
	private int rating; // 별점(1-5)

	@Column(nullable = false, length = 500)
	private String content; // 리뷰 내용

	private boolean deleted = false;


	private Review(Order order, User user, Store store, int rating, String content) {
		this.order = order;
		this.user = user;
		this.store = store;
		this.rating = rating;
		this.content = content;
	}

	public static Review created(Order order, User user, Store store, int rating, String content) {
		return new Review(order, user, store, rating, content);
	}
}