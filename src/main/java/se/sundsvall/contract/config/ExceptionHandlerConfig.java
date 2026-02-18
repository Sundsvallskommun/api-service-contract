package se.sundsvall.contract.config;

import com.turkraft.springfilter.parser.InvalidSyntaxException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zalando.problem.Problem;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.zalando.problem.Status.BAD_REQUEST;

/**
 * Global exception handler providing custom error responses for specific exception types.
 */
@RestControllerAdvice
public class ExceptionHandlerConfig {

	/**
	 * Handles invalid filter syntax from the spring-filter library.
	 *
	 * <p>
	 * Without this handler, InvalidSyntaxException is caught by dept44's generic ProblemHandler
	 * which returns HTTP 500 (Internal Server Error) instead of the correct HTTP 400 (Bad Request).
	 */
	@ExceptionHandler(InvalidSyntaxException.class)
	ResponseEntity<Problem> handleInvalidSyntaxException(final InvalidSyntaxException exception) {
		final var problem = Problem.builder()
			.withStatus(BAD_REQUEST)
			.withTitle("Invalid filter syntax")
			.withDetail(exception.getMessage())
			.build();

		return badRequest().body(problem);
	}
}
