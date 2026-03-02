package se.sundsvall.contract.service.diff;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import se.sundsvall.contract.model.Change;
import se.sundsvall.contract.model.Term;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.BooleanNode;
import tools.jackson.databind.node.IntNode;
import tools.jackson.databind.node.StringNode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.sundsvall.contract.model.Change.Type.ADDITION;
import static se.sundsvall.contract.model.Change.Type.MODIFICATION;
import static se.sundsvall.contract.model.Change.Type.REMOVAL;

@SpringBootTest(classes = {
	JacksonAutoConfiguration.class, Differ.class
})
class DifferTest {

	private static final String JSON = """
		{
			"description": "someText",
			"flag": true,
			"count": 5
		}
		""";

	@MockitoSpyBean
	private ObjectMapper objectMapper;

	@Autowired
	private Differ differ;

	private static Stream<TestCase> provideDiffTestData() {
		return Stream.of(
			new TestCase("""
				{"flag": true, "count": 5}
				""", REMOVAL, "$.description", new StringNode("someText"), null),
			new TestCase("""
				{"description": "someText", "flag": true, "count": 5, "name": 12345}
				""", ADDITION, "$.name", null, new IntNode(12345)),
			new TestCase("""
				{"description": "someText", "flag": false, "count": 5}
				""", MODIFICATION, "$.flag", BooleanNode.TRUE, BooleanNode.FALSE),
			new TestCase("""
				{"description": "someNewText", "flag": true, "count": 5}
				""", MODIFICATION, "$.description", new StringNode("someText"), new StringNode("someNewText")));
	}

	@Test
	void testDiff() throws Exception {
		differ.diff(Term.builder().build(), Term.builder().build(), List.of());

		verify(objectMapper, times(2)).writeValueAsString(any(Term.class));
	}

	@ParameterizedTest
	@MethodSource("provideDiffTestData")
	void testJsonDiff(final TestCase testCase) {
		final var changes = differ.diffJson(JSON, testCase.modifiedJson());

		assertThat(changes).isNotNull().hasSize(1).allSatisfy(change -> {
			assertThat(change.type()).isEqualTo(testCase.type);
			assertThat(change.path()).isEqualTo(testCase.path);
			assertThat(change.oldValue()).isEqualTo(testCase.oldValue);
			assertThat(change.newValue()).isEqualTo(testCase.newValue);
		});
	}

	@Test
	void testDiff_throwsException() {
		when(objectMapper.writeValueAsString(any())).thenThrow(new JacksonException("test") {});

		final var oldTerm = Term.builder().build();
		final var newTerm = Term.builder().build();
		final var excludedPaths = List.<String>of();

		assertThatThrownBy(() -> differ.diff(oldTerm, newTerm, excludedPaths))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("Unable to diff");
	}

	@Test
	void testJsonDiff_withExcludedPath() {
		final var modifiedJson = """
			{"flag": true, "count": 5}
			""";

		final var changes = differ.diffJson(JSON, modifiedJson, List.of("$.description"));

		assertThat(changes).isEmpty();
	}

	record TestCase(String modifiedJson, Change.Type type, String path, JsonNode oldValue, JsonNode newValue) {
	}
}
