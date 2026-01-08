package se.sundsvall.contract.service.diff;

import static java.util.Collections.emptyList;

import com.deblock.jsondiff.DiffGenerator;
import com.deblock.jsondiff.diff.JsonDiff;
import com.deblock.jsondiff.matcher.CompositeJsonMatcher;
import com.deblock.jsondiff.matcher.JsonMatcher;
import com.deblock.jsondiff.matcher.Path;
import com.deblock.jsondiff.matcher.StrictJsonArrayPartialMatcher;
import com.deblock.jsondiff.matcher.StrictJsonObjectPartialMatcher;
import com.deblock.jsondiff.matcher.StrictPrimitivePartialMatcher;
import com.deblock.jsondiff.viewer.JsonDiffViewer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import se.sundsvall.contract.model.Change;

@Component
public class Differ implements JsonDiffViewer {

	private static final JsonMatcher JSON_MATCHER = new CompositeJsonMatcher(
		new StrictJsonArrayPartialMatcher(),
		new StrictJsonObjectPartialMatcher(),
		new StrictPrimitivePartialMatcher());

	private final ObjectMapper objectMapper;
	private final List<Change> changes;

	public Differ(final ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		this.changes = new ArrayList<>();
	}

	public List<Change> diff(final Object oldObject, final Object newObject, final List<String> excludedPaths) {
		try {
			final var json1 = objectMapper.writeValueAsString(oldObject);
			final var json2 = objectMapper.writeValueAsString(newObject);

			return diffJson(json1, json2, excludedPaths);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("Unable to diff", e);
		}
	}

	public List<Change> diffJson(final String oldJson, final String newJson) {
		return diffJson(oldJson, newJson, emptyList());
	}

	public List<Change> diffJson(final String oldJson, final String newJson, final List<String> excludedPaths) {
		changes.clear();

		final var diff = DiffGenerator.diff(oldJson, newJson, JSON_MATCHER);
		diff.display(this);
		return changes.stream()
			.filter(change -> !excludedPaths.contains(change.path()))
			.toList();
	}

	@Override
	public void extraProperty(final Path path, final tools.jackson.databind.JsonNode extraReceivedValue) {
		changes.add(Change.addition(path, toFasterxmlJsonNode(extraReceivedValue)));
	}

	@Override
	public void missingProperty(final Path path, final tools.jackson.databind.JsonNode value) {
		changes.add(Change.removal(path, toFasterxmlJsonNode(value)));
	}

	@Override
	public void primaryNonMatching(final Path path, final tools.jackson.databind.JsonNode expected, final tools.jackson.databind.JsonNode value) {
		changes.add(Change.modification(path, toFasterxmlJsonNode(expected), toFasterxmlJsonNode(value)));
	}

	@Override
	public void nonMatchingProperty(final Path path, final JsonDiff diff) {
		diff.display(this);
	}

	@Override
	public void matchingProperty(final Path path, final JsonDiff diff) {
		// Intentionally empty
	}

	@Override
	public void primaryMatching(final Path path, final tools.jackson.databind.JsonNode value) {
		// Intentionally empty
	}

	// Necessary in order to convert between tools.jackson.databind.JsonNode and com.fasterxml.jackson.databind.JsonNode
	private JsonNode toFasterxmlJsonNode(tools.jackson.databind.JsonNode toolsNode) {
		return Optional.ofNullable(toolsNode)
			.map(node -> {
				try {
					return objectMapper.readTree(node.toString());
				} catch (Exception e) {
					throw new IllegalArgumentException("Failed to convert tools.jackson JsonNode", e);
				}
			})
			.orElse(null);
	}
}
