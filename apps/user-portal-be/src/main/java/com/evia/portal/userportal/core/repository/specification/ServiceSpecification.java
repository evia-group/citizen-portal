package com.evia.portal.userportal.core.repository.specification;


import com.evia.portal.userportal.core.domain.Category;
import com.evia.portal.userportal.core.domain.Location;
import com.evia.portal.userportal.core.domain.Service;
import com.evia.portal.userportal.core.repository.criteria.ServiceCriteria;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

public class ServiceSpecification {
    private ServiceSpecification() {
    }


    private static Specification<Service> orderByAsc() {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id"))).getRestriction();
    }
    private static Specification<Service> byName(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
            criteriaBuilder.lower(root.get("name")),
            "%".concat(name.toLowerCase()).concat("%")
        );
    }
    private static Specification<Service> byCategoryId(Long categoryId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Join<Service, Category> join = root.join("category");
            return criteriaBuilder.equal(join.get("id"), categoryId);
        };
    }

    private static Specification<Service> byLocationId(Long locationId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Join<Service, Location> join = root.join("location");
            return criteriaBuilder.equal(join.get("id"), locationId);
        };
    }

    public static Specification<Service> getSpecification(ServiceCriteria criteria) {
        Specification<Service> specification = null;

        specification = Objects.requireNonNull(Specification.where(specification)).and(orderByAsc());

        if (criteria.getCategoryId() != null) {
            specification = Objects.requireNonNull(Specification.where(specification))
                .and(byCategoryId(criteria.getCategoryId()));
        }
        if(criteria.getName() != null && !criteria.getName().isEmpty()){
            specification = Objects.requireNonNull(Specification.where(specification))
                .and(byName(criteria.getName()));
        }
        if (criteria.getLocationId() != null) {
            specification = Objects.requireNonNull(Specification.where(specification))
                .and(byLocationId(criteria.getLocationId()));
        }


        return specification;
    }

}
