package se.sundsvall.contract.service.diff;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.sundsvall.contract.model.Change.Type.ADDITION;
import static se.sundsvall.contract.model.Change.Type.MODIFICATION;
import static se.sundsvall.contract.model.Change.Type.REMOVAL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.flipkart.zjsonpatch.JsonPatch;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import se.sundsvall.contract.model.Change;
import se.sundsvall.contract.model.Term;

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
			new TestCase("[{'op': 'remove', 'path': '/description'}]", REMOVAL, "$.description", new TextNode("someText"), null),
			new TestCase("[{'op': 'add', 'path': '/name', 'value': 12345}]", ADDITION, "$.name", null, new IntNode(12345)),
			new TestCase("[{'op': 'replace', 'path': '/flag', 'value': false}]", MODIFICATION, "$.flag", BooleanNode.TRUE, BooleanNode.FALSE),
			new TestCase("[{'op': 'replace', 'path': '/description', 'value': 'someNewText'}]", MODIFICATION, "$.description", new TextNode("someText"), new TextNode("someNewText")));
	}

	@Test
	void testDiff() throws Exception {
		differ.diff(Term.builder().build(), Term.builder().build(), List.of());

		verify(objectMapper, times(2)).writeValueAsString(any(Term.class));
	}

	@ParameterizedTest
	@MethodSource("provideDiffTestData")
	void testJsonDiff(final TestCase testCase) {
		final var patch = toJsonNode(testCase.jsonPatch());
		final var newJson = JsonPatch.apply(patch, toJsonNode(JSON));
		final var changes = differ.diffJson(JSON, newJson.toString());

		assertThat(changes).isNotNull().hasSize(1).allSatisfy(change -> {
			assertThat(change.type()).isEqualTo(testCase.type);
			assertThat(change.path()).isEqualTo(testCase.path);
			assertThat(change.oldValue()).isEqualTo(testCase.oldValue);
			assertThat(change.newValue()).isEqualTo(testCase.newValue);
		});
	}

	@Test
	void testDiff_throwsException() throws Exception {
		when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("test") {});

		assertThatThrownBy(() -> differ.diff(Term.builder().build(), Term.builder().build(), List.of()))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("Unable to diff");
	}

	@Test
	void testJsonDiff_withExcludedPath() {
		final var testCase = provideDiffTestData().toList().getFirst();

		final var patch = toJsonNode(testCase.jsonPatch());
		final var newJson = JsonPatch.apply(patch, toJsonNode(JSON));
		final var changes = differ.diffJson(JSON, newJson.toString(), List.of("$.description"));

		assertThat(changes).isEmpty();
	}

	private JsonNode toJsonNode(final String json) {
		try {
			return objectMapper.readTree(json.replace("'", "\""));
		} catch (final JsonProcessingException e) {
			throw new RuntimeJsonProcessingException(e);
		}
	}

	record TestCase(String jsonPatch, Change.Type type, String path, JsonNode oldValue, JsonNode newValue) {
	}

	static class RuntimeJsonProcessingException extends RuntimeException {

		private static final long serialVersionUID = -2498824485966592543L;

		public RuntimeJsonProcessingException(final JsonProcessingException cause) {
			super(cause);
		}
	}
}
