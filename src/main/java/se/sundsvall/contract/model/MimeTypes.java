package se.sundsvall.contract.model;

import org.springframework.util.MimeTypeUtils;

/**
 * Grammar check for client-supplied mime types.
 *
 * <p>
 * An attachment's mime type is sent in by the client and is echoed back verbatim in the {@code Content-Type} response
 * header when the content is fetched. Anything that is not a well-formed mime type therefore has to be rejected before
 * it is stored, rather than left for the servlet container to deal with on the way out.
 *
 * <p>
 * A value is accepted only if it is a concrete {@code type/subtype} and nothing else. That is stricter than the RFC
 * grammar on two points, both of which exist because it is the <em>raw</em> string that reaches the header, while
 * Spring's parser reports on a normalized one:
 * <ul>
 * <li>Surrounding whitespace is rejected, since the parser trims it away. {@code "application/pdf<CR><LF>"} parses
 * cleanly as {@code application/pdf} - trim removes every character up to and including {@code U+0020} - and the raw
 * string would still carry the CRLF into the response.</li>
 * <li>Parameters are rejected, since the parser only token-checks a parameter value when it is unquoted. A quoted value
 * is free to carry control characters, so {@code text/plain;charset="<CR><LF>..."} parses cleanly too. The content is
 * always served as a download with {@code nosniff}, so no parameter carries any meaning here anyway.</li>
 * </ul>
 * What is left is a string of token characters with a single {@code /} in it, which is safe to store and to echo.
 */
public final class MimeTypes {

	private MimeTypes() {}

	/**
	 * Whether the given value is a concrete, parameter-less mime type on the form {@code type/subtype}, with no
	 * surrounding whitespace.
	 *
	 * @param  mimeType the value to check, may be null
	 * @return          true if the value is safe to store and to echo in a header
	 */
	public static boolean isValid(final String mimeType) {
		if (mimeType == null) {
			return false;
		}

		// Checked against the raw string, before the parser gets to normalize either of them away.
		if (!mimeType.equals(mimeType.trim()) || mimeType.indexOf(';') >= 0) {
			return false;
		}

		try {
			final var parsed = MimeTypeUtils.parseMimeType(mimeType);

			// A wildcard is a valid thing to *accept* but not a valid thing for a stored file to *be*.
			return !parsed.isWildcardType() && !parsed.isWildcardSubtype();
		} catch (final IllegalArgumentException e) {
			// InvalidMimeTypeException extends IllegalArgumentException, and the parser wraps everything it catches
			// from the MimeType constructor in one.
			return false;
		}
	}
}
