package se.sundsvall.contract.model;

import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

class MimeTypesTest {

	@ParameterizedTest
	@ValueSource(strings = {
		"application/pdf",
		"image/png",
		"text/plain",
		"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
		"APPLICATION/PDF",
		"application/vnd.api+json",
		"application/x-tar",
		"x-custom/x-thing"
	})
	void validMimeTypes(final String mimeType) {
		assertThat(MimeTypes.isValid(mimeType)).isTrue();
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {
		"",
		"   ",
		"application",                        // no subtype
		"application/",                       // empty subtype
		"/pdf",                               // empty type
		"application/pdf/extra",              // '/' is not a token character
		"application pdf",                    // no slash at all
		"appli cation/pdf",                   // space in the type
		"application/p df",                   // space in the subtype
		"appli\tcation/pdf",                  // tab in the type
		"application/pdf\0extra",             // embedded NUL
		"application/pdf@evil",               // '@' is a separator, not a token character
		"application/(pdf)",                  // parentheses are comment delimiters
		"application/pdfå",              // non-ASCII
		"<script>alert(1)</script>",          // garbage
		"application/pdf\r\nX-Injected: yes", // header injection
		"application/pdf\nX-Injected: yes",   // bare LF
		"*/*",                                // wildcard type and subtype
		"*",                                  // parsed as */*
		"application/*",                      // wildcard subtype
		"application/*+xml"                   // wildcard subtype with suffix
	})
	void invalidMimeTypes(final String mimeType) {
		assertThat(MimeTypes.isValid(mimeType)).isFalse();
	}

	/**
	 * These all parse cleanly - the parser trims the value before looking at it, and trim removes everything up to and
	 * including U+0020 - but the raw string is what reaches the response header, so they must not be accepted.
	 */
	@ParameterizedTest
	@ValueSource(strings = {
		"application/pdf ",
		" application/pdf",
		"application/pdf\r\n",
		"application/pdf\n",
		"application/pdf\t",
		"application/pdf\0",
		"\0application/pdf"
	})
	void surroundingWhitespaceIsRejectedEvenThoughItParses(final String mimeType) {
		assertThat(MimeTypes.isValid(mimeType.trim())).isTrue();
		assertThat(MimeTypes.isValid(mimeType)).isFalse();
	}

	/**
	 * Parameters are grammatically valid but rejected: a quoted parameter value is not token-checked by Spring, so it
	 * can carry anything at all.
	 */
	@ParameterizedTest
	@ValueSource(strings = {
		"text/plain;charset=utf-8",
		"text/plain; charset=utf-8",
		"application/pdf;name=\"x\"",
		"application/pdf;",
		"text/plain;charset=\"\r\nX-Injected: yes\""
	})
	void parametersAreRejected(final String mimeType) {
		assertThat(MimeTypes.isValid(mimeType)).isFalse();
	}

	@Test
	void noExceptionEscapes() {
		// The call sites treat false as "not a mime type" and have nothing to catch, so nothing may propagate.
		assertThatNoException().isThrownBy(() -> MimeTypes.isValid("text/plain;charset=no-such-charset"));
		assertThatNoException().isThrownBy(() -> MimeTypes.isValid(";"));
		assertThatNoException().isThrownBy(() -> MimeTypes.isValid("/"));
	}

	@Test
	void isAUtilityClass() throws Exception {
		assertThat(Modifier.isFinal(MimeTypes.class.getModifiers())).isTrue();

		final var constructor = MimeTypes.class.getDeclaredConstructor();
		assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();

		constructor.setAccessible(true);
		assertThat(constructor.newInstance()).isNotNull();
	}
}
