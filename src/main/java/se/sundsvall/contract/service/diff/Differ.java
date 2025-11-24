package se.sundsvall.contract.service.diff;

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
import org.springframework.stereotype.Component;
import se.sundsvall.contract.model.Change;

@Component
public class Differ implements JsonDiffViewer {

	private static final JsonMatcher JSON_MATCHER = new CompositeJsonMatcher(
		new StrictJsonArrayPartialMatcher(),
		new StrictJsonObjectPartialMatcher(),
		new StrictPrimitivePartialMatcher());

	private final ObjectMapper objectMapper;
	private final List<Change> changes = new ArrayList<>();

	public Differ(final ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
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
		return diffJson(oldJson, newJson, List.of());
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
	public void extraProperty(final Path path, final JsonNode extraReceivedValue) {
		changes.add(Change.addition(path, extraReceivedValue));
	}

	@Override
	public void missingProperty(final Path path, final JsonNode value) {
		changes.add(Change.removal(path, value));
	}

	@Override
	public void primaryNonMatching(final Path path, final JsonNode expected, final JsonNode value) {
		changes.add(Change.modification(path, expected, value));
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
	public void primaryMatching(final Path path, final JsonNode value) {
		// Intentionally empty
	}
}
