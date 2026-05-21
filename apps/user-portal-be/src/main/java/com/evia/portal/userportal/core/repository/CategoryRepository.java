package com.evia.portal.userportal.core.repository;

import com.evia.portal.userportal.core.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
