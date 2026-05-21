package com.evia.portal.userportal.core.repository.specification;

import com.evia.portal.userportal.core.domain.Application;
import com.evia.portal.userportal.core.domain.Service;
import com.evia.portal.userportal.core.repository.criteria.ApplicationCriteria;
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

    private static Specification<Application> orderByDesc() {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id"))).getRestriction();
    }

    public static Specification<Application> getSpecification(ApplicationCriteria criteria) {
        Specification<Application> specification = null;
        specification = Objects.requireNonNull(Specification.where(specification)).and(orderByDesc());
        if (criteria.getServiceId() != null) {
            specification = Objects.requireNonNull(Specification.where(specification))
                .and(byServiceId(criteria.getServiceId()));
        }
        return specification;
    }
}
