package com.evia.portal.adminportal.core.service;

import com.evia.portal.adminportal.core.domain.Domain;
import com.evia.portal.adminportal.core.exception.EntityNotFoundException;
import com.evia.portal.adminportal.core.exception.EntityNotValidException;
import com.evia.portal.adminportal.core.repository.DomainRepository;
import com.evia.portal.adminportal.core.repository.criteria.DomainCriteria;
import com.evia.portal.adminportal.core.repository.specification.DomainSpecification;
import com.evia.portal.adminportal.core.validator.DomainValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class DomainService {

  private static final String DOMAIN_NOT_FOUND = "Domain with id %d not found.";
  private final DomainRepository domainRepository;
  Logger logger = Logger.getLogger(getClass().getName());


  public List<Domain> getDomains(DomainCriteria criteria) {

    return domainRepository.findAll(DomainSpecification.getSpecification(criteria));
  }

  public Domain getDomainById(Long id) {

    return domainRepository.findById(id).orElseThrow(() ->
      new EntityNotFoundException(DOMAIN_NOT_FOUND.formatted(id))
    );
  }

  public Domain createDomain(Domain domain) {

    validateDomain(domain);
    return domainRepository.save(domain);
  }

  public void deleteDomain(Long id) {

    if (!domainRepository.existsById(id)) {
      throw new EntityNotFoundException(DOMAIN_NOT_FOUND.formatted(id));
    }
    domainRepository.deleteById(id);
  }

  public Domain updateDomain(Domain updateDomain, Long id) {

    validateDomain(updateDomain);
    return domainRepository.findById(id)
      .map(foundDomain -> {
        updateDomain.setId(foundDomain.getId());
        updateDomain.setVersion(foundDomain.getVersion());
        return domainRepository.save(updateDomain);
      })
      .orElseThrow(() ->
        new EntityNotFoundException(DOMAIN_NOT_FOUND.formatted(updateDomain.getId()))
      );
  }

  private void validateDomain(Domain domain) {

    final List<String> errors = DomainValidator.validateDomain(domain);
    if (!errors.isEmpty()) {
      logger.info(errors.getFirst());
      throw new EntityNotValidException("Domain validation failed", errors);
    }
  }
}
