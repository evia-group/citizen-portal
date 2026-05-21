package com.evia.portal.serviceportal.core.repository;

import com.evia.portal.serviceportal.core.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
