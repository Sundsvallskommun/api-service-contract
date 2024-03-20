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

class ContractHolderTest {

	@Test
	void testBean() {
		assertThat(ContractHolder.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {

		final List<Contract> landLeaseContracts = List.of(LandLeaseContract.builder().build());

		var contractHolder = ContractHolder.builder()
			.withLandLeaseContracts(landLeaseContracts)
			.build();

		assertThat(contractHolder).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(contractHolder.getLandLeaseContracts()).isEqualTo(landLeaseContracts);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(ContractHolder.builder().build()).hasAllNullFieldsOrProperties();
	}

}
