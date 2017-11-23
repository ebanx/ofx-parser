package com.ebanx.ofx.parser.lexer;

import com.ebanx.ofx.parser.OfxParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class OfxLexerImpl implements OfxLexer {

    private final BufferedReader bufferedReader;
    private final StringBuffer internalBuffer;


    public OfxLexerImpl(InputStream inputStream) {
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        this.internalBuffer = new StringBuffer();
    }

    @Override
    public Token nextToken() throws IOException {
        String line;
        if (internalBuffer.length() > 0) {
            line = internalBuffer.toString();
            internalBuffer.setLength(0);
        } else {
            line = bufferedReader.readLine();
        }

        if (line == null) {
            return null;
        }

        if (line.isEmpty()) {
            return nextToken();
        }

        return processLine(line);
    }

    public Token processLine(String line) throws IOException {
        int tagStartIdx = line.indexOf("<");

        if (tagStartIdx == -1) {
            return new Text(line);
        }

        String beforeTagStartContent = line.substring(0, tagStartIdx).trim();

        if (beforeTagStartContent.length() > 0) {
            internalBuffer.append(line.substring(tagStartIdx));

            return new Text(beforeTagStartContent);
        }

        String fromTagStart = line.substring(tagStartIdx);

        int tagCloseIdx = fromTagStart.indexOf(">");

        if (tagCloseIdx == -1) {
            throw new OfxParseException("Unclosed tag: " + fromTagStart);
        }

        Token token;
        if (fromTagStart.charAt(1) != '/') {
            // opening tag
            token = new OpenTag(fromTagStart.substring(1, tagCloseIdx));
        } else {
            // closing tag
            token = new CloseTag(fromTagStart.substring(2, tagCloseIdx));
        }

        internalBuffer.append(fromTagStart.substring(tagCloseIdx + 1));

        return token;
    }
}
