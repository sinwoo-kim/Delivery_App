package com.example.outsourcingproject.domain.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.outsourcingproject.domain.menu.entity.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long> {
	@Modifying
	@Query("UPDATE Menu m SET m.isDeleted = true WHERE m.store.storeId = :storeId")
	void softDeletedAllByStoreId(Long storeId);
}
