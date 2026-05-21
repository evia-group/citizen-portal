package com.evia.portal.userportal.core.service;

import com.evia.portal.userportal.core.domain.Domain;
import com.evia.portal.userportal.core.repository.DomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DomainService {

    private final DomainRepository domainRepository;

    public List<Domain> getDomains(){
        return domainRepository.findAll();
    }
}
