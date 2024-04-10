package se.sundsvall.contract.model;

import com.deblock.jsondiff.matcher.Path;
import com.fasterxml.jackson.databind.JsonNode;

public record Change(Type type, String path, JsonNode oldValue, JsonNode newValue) {

    public enum Type {
        ADDITION,
        REMOVAL,
        MODIFICATION
    }

    public static Change addition(final Path path, final JsonNode newValue) {
        return new Change(Type.ADDITION, path.toString(), null, newValue);
    }

    public static Change removal(final Path path, final JsonNode oldValue) {
        return new Change(Type.REMOVAL, path.toString(), oldValue, null);
    }

    public static Change modification(final Path path, final JsonNode oldValue, final JsonNode newValue) {
        return new Change(Type.MODIFICATION, path.toString(), oldValue, newValue);
    }
}
