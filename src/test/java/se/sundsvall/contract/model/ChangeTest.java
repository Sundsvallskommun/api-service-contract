package se.sundsvall.contract.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.deblock.jsondiff.matcher.Path;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.Test;

class ChangeTest {

    @Test
    void testConstructorAndGetters() {
        var type = Change.Type.MODIFICATION;
        var path = "$.someProperty";
        var oldValue = new TextNode("oldValue");
        var newValue = new TextNode("newValue");

        var change = new Change(type, path, oldValue, newValue);

        assertThat(change.type()).isEqualTo(type);
        assertThat(change.path()).isEqualTo(path);
        assertThat(change.oldValue()).isEqualTo(oldValue);
        assertThat(change.newValue()).isEqualTo(newValue);
    }

    @Test
    void testFactoryAddition() {
        var path = new Path().add(new Path.PathItem.ObjectProperty("someProperty"));
        var newValue = new TextNode("newValue");

        var change = Change.addition(path, newValue);

        assertThat(change.type()).isEqualTo(Change.Type.ADDITION);
        assertThat(change.path()).isEqualTo(path.toString());
        assertThat(change.oldValue()).isNull();
        assertThat(change.newValue()).isEqualTo(newValue);
    }

    @Test
    void testFactoryModification() {
        var path = new Path().add(new Path.PathItem.ObjectProperty("someProperty"));
        var oldValue = new TextNode("oldValue");
        var newValue = new TextNode("newValue");

        var change = Change.modification(path, oldValue, newValue);

        assertThat(change.type()).isEqualTo(Change.Type.MODIFICATION);
        assertThat(change.path()).isEqualTo(path.toString());
        assertThat(change.oldValue()).isEqualTo(oldValue);
        assertThat(change.newValue()).isEqualTo(newValue);
    }

    @Test
    void testFactoryRemoval() {
        var path = new Path().add(new Path.PathItem.ObjectProperty("someProperty"));
        var oldValue = new TextNode("oldValue");

        var change = Change.removal(path, oldValue);

        assertThat(change.type()).isEqualTo(Change.Type.REMOVAL);
        assertThat(change.path()).isEqualTo(path.toString());
        assertThat(change.oldValue()).isEqualTo(oldValue);
        assertThat(change.newValue()).isNull();
    }
}
