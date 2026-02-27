package se.sundsvall.contract.config;

import com.turkraft.springfilter.parser.InvalidSyntaxException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

class ExceptionHandlerConfigTest {

	private final ExceptionHandlerConfig exceptionHandlerConfig = new ExceptionHandlerConfig();

	@Test
	void handleInvalidSyntaxException() {
		final var exception = new InvalidSyntaxException("Invalid filter: unexpected token");

		final var response = exceptionHandlerConfig.handleInvalidSyntaxException(exception);

		assertThat(response.getStatusCode().value()).isEqualTo(BAD_REQUEST.value());
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getBody().getTitle()).isEqualTo("Invalid filter syntax");
		assertThat(response.getBody().getDetail()).isEqualTo("Invalid filter: unexpected token");
	}
}
