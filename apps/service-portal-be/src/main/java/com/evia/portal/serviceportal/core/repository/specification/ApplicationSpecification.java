package com.evia.portal.serviceportal.core.repository.specification;

import com.evia.portal.serviceportal.core.domain.Application;
import com.evia.portal.serviceportal.core.domain.Service;
import com.evia.portal.serviceportal.core.repository.criteria.ApplicationCriteria;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

public class ApplicationSpecification {

    private ApplicationSpecification() {
    }

    private static Specification<Application> byServiceId(Long serviceId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Join<Application, Service> join = root.join("service");
            return criteriaBuilder.equal(join.get("id"), serviceId);
        };
    }

    private static Specification<Application> orderByAsc() {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id"))).getRestriction();
    }

    public static Specification<Application> getSpecification(ApplicationCriteria criteria) {
        Specification<Application> specification = null;
        specification = Objects.requireNonNull(Specification.where(specification)).and(orderByAsc());
        if (criteria.getServiceId() != null) {
            specification = Objects.requireNonNull(Specification.where(specification))
                .and(byServiceId(criteria.getServiceId()));
        }
        return specification;
    }
}
