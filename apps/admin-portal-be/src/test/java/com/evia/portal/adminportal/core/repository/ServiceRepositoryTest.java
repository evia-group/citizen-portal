package com.evia.portal.adminportal.core.repository;

import com.evia.portal.adminportal.core.domain.Category;
import com.evia.portal.adminportal.core.domain.Location;
import com.evia.portal.adminportal.core.domain.Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: fix and refactor to use mocks
@ExtendWith(SpringExtension.class)
@DataJpaTest
class ServiceRepositoryTest {

  public static final String SERVICE_1 = "Service1";
  public static final String SERVICE_2 = "Service2";
  public static final String SERVICE_3 = "Service3";

  public static final Long TEST_COST = 10L;

  public static List<Service> serviceList = new ArrayList<>();
  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private ServiceRepository serviceRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private LocationsRepository locationRepository;


  @Test
  void NoCriteriaGetAll_ThenReturnAll() {

    final List<Location> locationList = locationRepository.findAll();
    final List<Category> categoryList = categoryRepository.findAll();

    int existingServices = serviceRepository.findAll().size();
    createSampleCategories(categoryList.get(0), categoryList.get(1), locationList.get(0), locationList.get(1));

    serviceList = serviceRepository.findAll();
    assertThat(serviceList).hasSize(3 + existingServices);
  }


  public void createSampleCategories(Category category1, Category category2, Location location1, Location location2) {

    final Service service1 = Service.builder()
      .name(SERVICE_1)
      .location(location1)
      .category(category1)
      .cost(TEST_COST)
      .build();

    entityManager.persistAndFlush(service1);

    final Service service2 = Service.builder()
      .name(SERVICE_2)
      .location(location2)
      .category(category2)
      .cost(TEST_COST)
      .build();

    entityManager.persistAndFlush(service2);

    final Service service3 = Service.builder()
      .name(SERVICE_3)
      .location(location2)
      .category(category2)
      .cost(TEST_COST)
      .build();

    entityManager.persistAndFlush(service3);

    serviceList.add(service1);
    serviceList.add(service2);
    serviceList.add(service3);
  }
}
