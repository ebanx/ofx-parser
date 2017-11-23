package com.ebanx.ofx.parser.lexer;

import java.io.IOException;

public interface OfxLexer {

    /**
     * Read the next token from the input stream.
     * The returning token can be any of the following types:
     * - {@link Text} for text content
     * - {@link OpenTag} when a tag is open
     * - {@link CloseTag} when a tag is closed
     *
     * The tokens are scanned line by line, so if there is a text content that spans to two lines
     * two {@link Text} objects will be created and returned separately.
     *
     * Empty spaces are not trimmed, so if there is a tag with just empty spaces (e.g. "<     >")
     * a {@link OpenTag} will be returned with the spaces as `tagName`
     *
     * @return the next token found in the input stream.
     * @throws IOException in case of failure when reading the input stream.
     */
    Token nextToken() throws IOException;
}
