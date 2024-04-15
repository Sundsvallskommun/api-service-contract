package se.sundsvall.contract.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import se.sundsvall.dept44.models.api.paging.PagingMetaData;

class ContractPaginatedResponseTest {

	@Test
	void testBean() {
		assertThat(ContractPaginatedResponse.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		var response = ContractPaginatedResponse.builder()
			.withContracts(List.of(LandLeaseContract.builder().build()))
			.withMetaData(new PagingMetaData())
			.build();

		assertThat(response.getContracts()).isNotNull();
		assertThat(response.getMetaData()).isNotNull();
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(ContractPaginatedResponse.builder().build()).hasAllNullFieldsOrProperties();
	}

}
