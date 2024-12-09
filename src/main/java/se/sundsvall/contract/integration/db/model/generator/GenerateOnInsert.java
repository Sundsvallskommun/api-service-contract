package se.sundsvall.contract.integration.db.model.generator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.hibernate.annotations.ValueGenerationType;

@ValueGenerationType(generatedBy = ContractIdGenerator.class)
@Retention(RUNTIME)
@Target({
	FIELD
})
public @interface GenerateOnInsert {

}
