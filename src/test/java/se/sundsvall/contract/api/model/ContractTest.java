package se.sundsvall.contract.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import org.assertj.core.api.Assertions;
import org.geojson.FeatureCollection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.sundsvall.contract.model.ExtraParameterGroup;
import se.sundsvall.contract.model.Fees;
import se.sundsvall.contract.model.TermGroup;
import se.sundsvall.contract.model.enums.ContractType;
import se.sundsvall.contract.model.enums.LeaseType;
import se.sundsvall.contract.model.enums.Status;

class ContractTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), LocalDate.class);
	}

	@Test
	void testBean() {
		assertThat(Contract.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testSetAndGetAllFields() {
		var version = 1;
		var contractId = "2024-12345";
		var description = "A simple description of the contract";
		var externalReferenceId = "123";
		var leaseType = LeaseType.LAND_LEASE_PUBLIC;
		var municipalityId = "1984";
		var objectIdentity = "909a6a80-d1a4-90ec-e040-ed8f66444c3f";
		var status = Status.ACTIVE;
		var type = ContractType.PURCHASE_AGREEMENT;
		var leasehold = new Leasehold();
		var attachmentMetaData = List.of(new AttachmentMetaData());
		var additionalTerms = List.of(new TermGroup());
		var extraParameters = List.of(new ExtraParameterGroup());
		var indexTerms = List.of(new TermGroup());
		var propertyDesignations = List.of("SUNDSVALL NORRMALM 1:1", "SUNDSVALL NORRMALM 1:2");
		var stakeholders = List.of(new Stakeholder());
		var notices = List.of(new Notice());
		var duration = Duration.builder().build();
		var extension = Extension.builder().build();
		var fees = Fees.builder().build();
		var invoicing = new Invoicing();
		var start = LocalDate.of(2020, 1, 1);
		var end = LocalDate.of(2022, 12, 31);
		var area = 150;
		var signedByWitness = true;
		var areaData = new FeatureCollection();

		// Create a new Contract object
		var contract = Contract.builder()
			.withVersion(version)
			.withContractId(contractId)
			.withDescription(description)
			.withExternalReferenceId(externalReferenceId)
			.withLeaseType(leaseType)
			.withMunicipalityId(municipalityId)
			.withObjectIdentity(objectIdentity)
			.withStatus(status)
			.withType(type)
			.withLeasehold(leasehold)
			.withAttachmentMetaData(attachmentMetaData)
			.withAdditionalTerms(additionalTerms)
			.withExtraParameters(extraParameters)
			.withIndexTerms(indexTerms)
			.withPropertyDesignations(propertyDesignations)
			.withStakeholders(stakeholders)
			.withDuration(duration)
			.withFees(fees)
			.withInvoicing(invoicing)
			.withStart(start)
			.withEnd(end)
			.withExtension(extension)
			.withNotices(notices)
			.withArea(area)
			.withSignedByWitness(signedByWitness)
			.withAreaData(areaData)
			.build();

		// Verify all fields
		assertThat(contract.getVersion()).isEqualTo(version);
		assertThat(contract.getContractId()).isEqualTo(contractId);
		assertThat(contract.getDescription()).isEqualTo(description);
		assertThat(contract.getExternalReferenceId()).isEqualTo(externalReferenceId);
		assertThat(contract.getLeaseType()).isEqualTo(leaseType);
		assertThat(contract.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(contract.getObjectIdentity()).isEqualTo(objectIdentity);
		assertThat(contract.getStatus()).isEqualTo(status);
		assertThat(contract.getType()).isEqualTo(type);
		assertThat(contract.getLeasehold()).isEqualTo(leasehold);
		assertThat(contract.getAttachmentMetaData()).isEqualTo(attachmentMetaData);
		assertThat(contract.getAdditionalTerms()).isEqualTo(additionalTerms);
		assertThat(contract.getExtraParameters()).isEqualTo(extraParameters);
		assertThat(contract.getIndexTerms()).isEqualTo(indexTerms);
		assertThat(contract.getPropertyDesignations()).isEqualTo(propertyDesignations);
		assertThat(contract.getStakeholders()).isEqualTo(stakeholders);
		assertThat(contract.getDuration()).isEqualTo(duration);
		assertThat(contract.getFees()).isEqualTo(fees);
		assertThat(contract.getInvoicing()).isEqualTo(invoicing);
		assertThat(contract.getStart()).isEqualTo(start);
		assertThat(contract.getEnd()).isEqualTo(end);
		assertThat(contract.getExtension()).isEqualTo(extension);
		assertThat(contract.getNotices()).isEqualTo(notices);
		assertThat(contract.getArea()).isEqualTo(area);
		assertThat(contract.isSignedByWitness()).isEqualTo(signedByWitness);
		assertThat(contract.getAreaData()).isEqualTo(areaData);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		Assertions.assertThat(Contract.builder().build())
			.hasAllNullFieldsOrPropertiesExcept("version", "signedByWitness");
	}
}
