package com.evia.portal.adminportal.core.repository;

import com.evia.portal.adminportal.core.domain.Category;
import com.evia.portal.adminportal.core.domain.Domain;
import com.evia.portal.adminportal.core.repository.criteria.CategoryCriteria;
import com.evia.portal.adminportal.core.repository.specification.CategorySpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class CategoryRepositoryTest {

  public static final String CATEGORY_1 = "Category1";
  public static final String CATEGORY_2 = "Category2";
  public static final String CATEGORY_3 = "Category3";

  public static List<Category> categoryList = new ArrayList<>();
  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private DomainRepository domainRepository;


  @Test
  void NoCriteriaGetAll_ThenReturnAll() {

    final List<Domain> domainList = domainRepository.findAll();
    List<Category> createdCategories = createSampleCategories(domainList.get(0), domainList.get(1), domainList.get(2));

    categoryList = categoryRepository.findAll();

    for (Category category : createdCategories) {
      assertThat(categoryList).contains(category);
    }
  }

  @Test
  void CategoryNameCriteria_ThenReturnByCategoryName() {

    final List<Domain> domainList = domainRepository.findAll();
    createSampleCategories(domainList.get(0), domainList.get(1), domainList.get(2));

    final CategoryCriteria criteria = CategoryCriteria.builder()
      .name(CATEGORY_1)
      .build();

    final List<Category> categoryList = categoryRepository.findAll(CategorySpecification.getSpecification(criteria));

    assertThat(categoryList).hasSize(1);
    assertThat(categoryList.getFirst().getName()).isEqualTo(CATEGORY_1);
  }

  @Test
  void DomainNameCriteria_ThenReturnByDomainName() {

    final List<Domain> domainList = domainRepository.findAll();
    createSampleCategories(domainList.get(0), domainList.get(1), domainList.get(1));


    final CategoryCriteria criteria = CategoryCriteria.builder()
      .domainName(domainList.get(0).getName())
      .build();

    final List<Category> categoryList = categoryRepository.findAll(CategorySpecification.getSpecification(criteria));

    assertThat(categoryList).isNotEmpty();


    final CategoryCriteria criteria2 = CategoryCriteria.builder()
      .domainName(domainList.get(1).getName())
      .build();

    final List<Category> categoryList2 = categoryRepository.findAll(CategorySpecification.getSpecification(criteria2));

    assertThat(categoryList2).isNotEmpty();
  }

  @Test
  void DomainIdCriteria_ThenReturnByDomainId() {

    final List<Domain> domainList = domainRepository.findAll();
    createSampleCategories(domainList.get(0), domainList.get(1), domainList.get(1));


    final CategoryCriteria criteria = CategoryCriteria.builder()
      .domainId(domainList.get(0).getId())
      .build();

    final List<Category> categoryList = categoryRepository.findAll(CategorySpecification.getSpecification(criteria));

    assertThat(categoryList).isNotEmpty();

    final CategoryCriteria criteria2 = CategoryCriteria.builder()
      .domainId(domainList.get(1).getId())
      .build();

    final List<Category> categoryList2 = categoryRepository.findAll(CategorySpecification.getSpecification(criteria2));

    assertThat(categoryList2).isNotEmpty();
  }


  @Test
  void WrongCategoryNameCriteria_ThenReturnEmpty() {

    final List<Domain> domainList = domainRepository.findAll();
    createSampleCategories(domainList.get(0), domainList.get(1), domainList.get(2));

    final CategoryCriteria criteria = CategoryCriteria.builder()
      .name("NotExistingCategoryName")
      .build();

    final List<Category> categoryList = categoryRepository.findAll(CategorySpecification.getSpecification(criteria));

    assertThat(categoryList).isEmpty();
  }

  public List<Category> createSampleCategories(Domain domain1, Domain domain2, Domain domain3) {

    final Category category1 = Category.builder()
      .name(CATEGORY_1)
      .domain(domain1)
      .build();

    entityManager.persistAndFlush(category1);

    final Category category2 = Category.builder()
      .name(CATEGORY_2)
      .domain(domain2)
      .build();

    entityManager.persistAndFlush(category2);

    final Category category3 = Category.builder()
      .name(CATEGORY_3)
      .domain(domain3)
      .build();

    entityManager.persistAndFlush(category3);

    categoryList.add(category1);
    categoryList.add(category2);
    categoryList.add(category3);

    return List.of(category1, category2, category3);
  }
}
