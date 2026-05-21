package com.evia.portal.userportal.core.repository.specification;


import com.evia.portal.userportal.core.domain.Application;
import com.evia.portal.userportal.core.domain.Comment;
import com.evia.portal.userportal.core.repository.criteria.CommentCriteria;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

public class CommentSpecification {

    private CommentSpecification() {
    }

    private static Specification<Comment> byApplicationId(Long applicationId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Join<Comment, Application> join = root.join("application");
            return criteriaBuilder.equal(join.get("id"), applicationId);
        };
    }

    private static Specification<Comment> orderByAsc() {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id"))).getRestriction();
    }

    public static Specification<Comment> getSpecification(CommentCriteria criteria) {
        Specification<Comment> specification = null;
        specification = Objects.requireNonNull(Specification.where(specification)).and(orderByAsc());
        if (criteria.getApplicationId() != null) {
            specification = Objects.requireNonNull(Specification.where(specification))
                .and(byApplicationId(criteria.getApplicationId()));
        }
        return specification;
    }
}
